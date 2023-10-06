package com.yhq.bishe.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhq.bishe.service.TeamService;
import com.yhq.bishe.common.BaseResponse;
import com.yhq.bishe.common.DeleteRequest;
import com.yhq.bishe.common.ErrorCode;
import com.yhq.bishe.common.ResultUtils;
import com.yhq.bishe.exception.BusinessException;
import com.yhq.bishe.model.domain.Team;
import com.yhq.bishe.model.domain.User;
import com.yhq.bishe.model.domain.UserTeam;
import com.yhq.bishe.model.domain.request.AddTeamRequest;
import com.yhq.bishe.model.domain.request.JoinTeamRequest;
import com.yhq.bishe.model.domain.request.QuitTeamRequest;
import com.yhq.bishe.model.domain.request.UpdateTeamRequest;
import com.yhq.bishe.model.dto.TeamQuery;
import com.yhq.bishe.model.vo.TeamUserVO;
import com.yhq.bishe.service.UserService;
import com.yhq.bishe.service.UserTeamService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: bisheBackend-master
 * @description: 用于编写组队相关的接口
 * @author: HenryYang
 * @create: 2023-03-19 16:56
 **/
@RestController
@RequestMapping("/team")
@CrossOrigin(allowCredentials = "true", origins = {"http://localhost:5173","http://127.0.0.1:5173"} )
public class TeamController {
    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;

    @Resource
    private UserTeamService userTeamService;

    /**
     *  创建一个队伍
     * @param addTeamRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody AddTeamRequest addTeamRequest, HttpServletRequest request){
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team,addTeamRequest);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        if(team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long teamId = teamService.addTeam(team,loginUser);
        return ResultUtils.success(teamId);
    }

    /**
     * 删除队伍 解散队伍
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request){
        if(deleteRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long teamId = deleteRequest.getId();
        if(teamId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean remove = teamService.deleteTeam(teamId,loginUser );
        if(!remove){
            throw new BusinessException(
                    ErrorCode.SYSTEM_ERROR,"删除失败"
            );
        }
        return ResultUtils.success(true);

    }

    /**
     * 更新队伍消息
     * @param updateTeamRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updadeTeam(@RequestBody UpdateTeamRequest updateTeamRequest, HttpServletRequest request){
//        传入参数是否为空
        if(updateTeamRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateById(updateTeamRequest,loginUser);
        if(!result){
            throw new BusinessException(
                    ErrorCode.SYSTEM_ERROR,"更新失败"
            );
        }
        return ResultUtils.success(true);

    }

    /**
     * 通过id获取队伍信息
     * @param teamId
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long teamId){
        if(teamId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team result = teamService.getById(teamId);
        if(result == null){
            throw new BusinessException(
                    ErrorCode.SYSTEM_ERROR,"获取队伍信息失败"
            );
        }
        return ResultUtils.success(result);
    }

    /**
     * 获取队伍信息列表
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery, HttpServletRequest request){
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
//        1.查询队伍列表
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, isAdmin);
//        2.查询已登录用户是否加入队伍
        // 获取teamId的列表
        List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        try{
            User loginUser = userService.getLoginUser(request);
            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.eq("userId",loginUser.getUserId());
            if(!CollectionUtils.isEmpty( teamIdList)) {
                userTeamQueryWrapper.in("teamId", teamIdList);
            }
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
            //当前用户已经加入的队伍的id的集合
            Set<Long> hasJoinTeamSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            //给teamList中的teamuser对vo象填充hasJoin属性
            teamList.forEach(teamUserVO -> {
                boolean hasJoin = hasJoinTeamSet.contains(teamUserVO.getId());
                teamUserVO.setHasJoined(hasJoin);
                }
            );
        }catch (Exception e){
//            SQLSyntaxErrorException
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"查询数据库时错误");
        }
//        3，查询各个队伍目前加入的人数
        QueryWrapper<UserTeam> userJoinTeamQueryWrapper = new QueryWrapper<>();
        if(!CollectionUtils.isEmpty( teamIdList)) {
            userJoinTeamQueryWrapper.in("teamId", teamIdList);
        }
        List<UserTeam> userTeamList = userTeamService.list(userJoinTeamQueryWrapper);
        //队伍id -> userTeam的映射
        Map<Long, List<UserTeam>> teamIdUserTeamList = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        teamList.forEach(teamUserVO -> {
                    int hasJoinNum = teamIdUserTeamList.getOrDefault(teamUserVO.getId(),new ArrayList<>()).size() ;
                    teamUserVO.setHasJoinNum(hasJoinNum);
                }
        );
        return ResultUtils.success(teamList);

    }

    /**
     * 获取我创建的队伍
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/create")
    public BaseResponse<List<TeamUserVO>> listCreateTeams(TeamQuery teamQuery, HttpServletRequest request){
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NO_AUTH,"用户未登录");
        }
        //队伍的userid = 当前用户的id 即为创建者
        teamQuery.setUserId(loginUser.getUserId());

        return this.listTeams(teamQuery,request);

    }
    /**
     * 获取我加入的队伍
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/join")
    public BaseResponse<List<TeamUserVO>> listJoinTeams(TeamQuery teamQuery, HttpServletRequest request){
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NO_AUTH,"用户未登录");
        }
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId",loginUser.getUserId());
//        当前用户加入的所有的队伍
        List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
//      todo 理解这段代码
//       队伍id -> userTeam的映射
        Map<Long, List<UserTeam>> listMap = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        Set<Long> keySet = listMap.keySet();
        List<Long> teamIdList = new ArrayList<>(keySet);
        teamQuery.setIdList(teamIdList);

        return this.listTeams(teamQuery,request);
    }

    /**
     * 获取分页后的队伍信息
     * @param teamQuery
     * @return
     */
//    todo 查询分页
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery){
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        如果copyProperties用不了就换成spring自带的beanUtils
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team,teamQuery);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>(team);
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        Page<Team> teamPage = teamService.page( page,teamQueryWrapper);
        return ResultUtils.success(teamPage);

    }

    /**
     * 用户加入某个队伍
     * @param joinTeamRequest
     * @param request
     * @return
     */
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody JoinTeamRequest joinTeamRequest, HttpServletRequest request){
        if(joinTeamRequest == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result =  teamService.joinTeam(joinTeamRequest,loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 用户退出某个队伍
     * @param quitTeamRequest
     * @param request
     * @return
     */
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody QuitTeamRequest quitTeamRequest, HttpServletRequest request){
        if(quitTeamRequest == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result =  teamService.quitTeam(quitTeamRequest,loginUser);
        return ResultUtils.success(result);
    }

}