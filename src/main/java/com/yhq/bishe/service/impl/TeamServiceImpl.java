package com.yhq.bishe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhq.bishe.exception.BusinessException;
import com.yhq.bishe.model.domain.enums.TeamStatusEnum;
import com.yhq.bishe.model.dto.TeamQuery;
import com.yhq.bishe.service.TeamService;
import com.yhq.bishe.common.ErrorCode;
import com.yhq.bishe.model.domain.Team;
import com.yhq.bishe.mapper.TeamMapper;
import com.yhq.bishe.model.domain.UserTeam;
import com.yhq.bishe.model.domain.request.JoinTeamRequest;
import com.yhq.bishe.model.domain.request.QuitTeamRequest;
import com.yhq.bishe.model.domain.request.UpdateTeamRequest;
import com.yhq.bishe.model.vo.TeamUserVO;
import com.yhq.bishe.model.vo.UserVO;
import com.yhq.bishe.service.UserService;
import com.yhq.bishe.service.UserTeamService;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import com.yhq.bishe.model.domain.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Henry
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2023-03-19 16:39:20
 */
@Service
@Data
@Transactional(rollbackFor = Exception.class)
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {
    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserService userService;
    @Resource
    private RedissonClient redissonClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        //1.判断请求参数是否为空
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        2.校验是否登录
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录，请先登录");
        }
        final long userId = loginUser.getUserId();

//        3.校验信息
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名称不满足要求");
        }
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长，不满足要求");
        }
//       status默认值为0 即公开队伍
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }
        //如果status为加密状态 则必须要设置密码
        String password = team.getPassword();
        if (statusEnum.SECRET.equals(statusEnum)) {
            if (password.length() > 32 || StringUtils.isBlank(password)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码为空或者过长");
            }
        }
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "过期时间 > 当前时间");
        }

        //一个用户最多只能创建或加入5个不同的队伍
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId);
        long count = userTeamService.count(userTeamQueryWrapper);
        if (count >= 5) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "一个用户最多只能创建或加入5个不同的队伍");
        }

//      事务的原子性 必须保持一致：同时插入team表和用户队伍关系表中
        team.setId(null);
        team.setUserId(userId);
        boolean save = this.save(team);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        }
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        Long teamId = team.getId();
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        save = userTeamService.save(userTeam);
        if (!save || teamId == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        }
        return teamId;

    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
//       1.组合查询条件
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if (queryWrapper != null) {
//          跟读队伍id查询
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
//           根据队伍idList查询
            List<Long> idList = teamQuery.getIdList();
            if (!CollectionUtils.isEmpty(idList)) {
                queryWrapper.in("id", idList);
            }
//            查询最大人数相等的
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }

//            根据创建人查询
            Long userId = teamQuery.getUserId();
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }
//           根据名称查询
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }

//            根据描述查询
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }
            //根据搜索关键词查询
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            //所有得人都可以看到加密的队伍 但仅管理员可以看到私密的队伍
            Integer status = teamQuery.getStatus();
            TeamStatusEnum enumByValue = TeamStatusEnum.getEnumByValue(status);
//            默认查询公开的
            if (enumByValue == null) {
                enumByValue = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && enumByValue.equals(TeamStatusEnum.PRIVATE)) {
                throw new BusinessException(ErrorCode.NO_AUTH, "没有查询权限");
            }
            queryWrapper.eq("status", enumByValue.getValue());
        }
//        不显示已经过期的队伍信息
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        List<Team> teamList = this.list(queryWrapper);
//        2.关联查询用户信息
//        查询前必须判空
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<TeamUserVO>();
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        for (Team team : teamList) {
//            查询用户名
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
//            通过用户名获取用户对象
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            UserVO userVO = new UserVO();
            try {
                BeanUtils.copyProperties(userVO, user);
                BeanUtils.copyProperties(teamUserVO, team);
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询失败");

            }
            teamUserVO.setCreateUser(userVO);
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public boolean updateById(UpdateTeamRequest updateTeamRequest, User loginUser) {

        if (updateTeamRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
//        判断队伍是否存在
        Long id = updateTeamRequest.getId();
        Team oldTeam = this.getTeamById(id);
//        是否有更新权限：管理员或者队伍创建者
        if (!userService.isAdmin(loginUser) && loginUser.getUserId() != oldTeam.getUserId()) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
        }
//       如果status为加密 那么必须设置密码 同理，如果status为公开 密码必须为空 创建队伍同理
        Integer updateStatus = updateTeamRequest.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(updateStatus);
        if (TeamStatusEnum.PUBLIC.equals(statusEnum)) {
//            需要抛出异常吗？这里选择直接过滤
            updateTeamRequest.setPassword("");
        } else if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            String password = updateTeamRequest.getPassword();
            if (StringUtils.isBlank(password)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密房间必须设置密码");
            }
        }
//        todo 如果新值和旧值一样 无需更新
//        执行更新
        Team newTeam = new Team();
        try {
            BeanUtils.copyProperties(newTeam, updateTeamRequest);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        boolean result = this.updateById(newTeam);
        return result;

    }

    @Override
    public boolean joinTeam(JoinTeamRequest joinTeamRequest, User loginUser) {
        if (joinTeamRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        long userId = loginUser.getUserId();
        Long teamId = joinTeamRequest.getTeamId();
        Team team = this.getTeamById(teamId);

//        队伍必须未过期
        if (team.getExpireTime() != null && team.getExpireTime().before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }

        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(team.getStatus());
//        禁止加入私密队伍
        if (TeamStatusEnum.PRIVATE.equals(statusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该队伍不允许加入");
        }
//        加入 加密的队伍
        String password = joinTeamRequest.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }
//        分布式锁
        RLock lock = redissonClient.getLock("yupao:joinTeam");
        try {
            while (true) {
                if (lock.tryLock(0, 30000, TimeUnit.MILLISECONDS)) {
                    //        一个用户最多只能创建或者加入5个不同的队伍
                    QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                    userTeamQueryWrapper.eq("userId", userId);
                    long count = userTeamService.count(userTeamQueryWrapper);
                    if (count >= 5) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "一个用户最多只能创建或加入5个不同的队伍");
                    }
                    //      不能重复加入已经加入的队伍
                    userTeamQueryWrapper = new QueryWrapper<>();
                    userTeamQueryWrapper.eq("teamId", teamId);
                    userTeamQueryWrapper.eq("userId", userId);
                    long hasUserJoinTeam = userTeamService.count(userTeamQueryWrapper);
                    if (hasUserJoinTeam >= 1) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已在该队伍，不能重复加入");
                    }
                    //       不能加入已经满员的队伍
                    long usersNum = this.countTeamUserByTeamId(teamId);
                    if (usersNum >= team.getMaxNum()) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满员");
                    }

                    UserTeam userTeam = new UserTeam();
                    userTeam.setUserId(userId);
                    userTeam.setJoinTime(new Date());
                    userTeam.setTeamId(teamId);
                    return userTeamService.save(userTeam);
                }
            }
        } catch (
                InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
//            throw new RuntimeException(e);
        } finally {
            //判断一下是否为自己加的锁 不能unlock别人的锁
//            一定要写进finally里面 不然如果程序执行过程中报错了也可以执行。
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                System.out.println();
            }
        }
        //        单机锁
        //    synchronized (this)

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(QuitTeamRequest quitTeamRequest, User loginUser) {
        if (quitTeamRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = quitTeamRequest.getTeamId();
        long userId = loginUser.getUserId();
        Team team = this.getTeamById(teamId);
//        如果用户不再队伍中 直接返回
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId);
        userTeamQueryWrapper.eq("teamId", teamId);
        long count = userTeamService.count(userTeamQueryWrapper);
        if (count < 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在或者用户不在队伍中");
        }
        long usersNum = this.countTeamUserByTeamId(teamId);
//        if(usersNum <= 0){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
//        }
        if (usersNum == 1) {
//            如果仅剩一人 则需要额外删除队伍信息
            QueryWrapper<Team> teamqueryWrapper = new QueryWrapper<>();
            teamqueryWrapper.eq("id", teamId);
            boolean remove = this.remove(teamqueryWrapper);
            if (!remove) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍信息失败");
            }
        } else {
//           队伍中至少剩下2人
            if (team.getUserId() == userId) {
//             原队长退出， 把队长移交给其他用户
                QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("teamId", teamId);
                queryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }

//              队长加入时间最早 所以第二早的就是新队长
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextLeaderId = nextUserTeam.getUserId();
//                将队长转移给 加入时间最早的队员
                team.setUserId(nextLeaderId);
                boolean result = this.updateById(team);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更换队伍队长时失败");
                }
            }
        }
//            删除用户队伍关系信息
        return userTeamService.remove(userTeamQueryWrapper);
    }

    /**
     * 解散队伍 删除队伍
     *
     * @param teamId
     * @param loginUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long teamId, User loginUser) {
        Team team = this.getTeamById(teamId);
        if (loginUser.getUserId() != team.getUserId()) {
//            当前用户不是队长
            throw new BusinessException(ErrorCode.FORBBIDEN, "本用户没有解散队伍的权限");
        }
//        删除用户队伍关联信息
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        boolean result = userTeamService.remove(userTeamQueryWrapper);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除用户队伍关联信息失败");
        }
//        删除队伍信息
        QueryWrapper<Team> teamqueryWrapper = new QueryWrapper<>();
        teamqueryWrapper.eq("id", teamId);
        boolean remove = this.remove(teamqueryWrapper);
        return remove;
    }

    /**
     * 获取某个队伍的总人数
     *
     * @param teamId
     * @return
     */
    private long countTeamUserByTeamId(long teamId) {
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        return userTeamService.count(userTeamQueryWrapper);
    }

    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        return team;
    }
}




