package com.yhq.bishe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhq.bishe.model.domain.Team;
import com.yhq.bishe.model.domain.User;
import com.yhq.bishe.model.domain.request.JoinTeamRequest;
import com.yhq.bishe.model.domain.request.QuitTeamRequest;
import com.yhq.bishe.model.domain.request.UpdateTeamRequest;
import com.yhq.bishe.model.dto.TeamQuery;
import com.yhq.bishe.model.vo.TeamUserVO;

import java.util.List;

/**
* @author Henry
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-03-19 16:39:20
*/
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam( Team team ,User loginUser);

    /**
     * 搜索队伍
     *
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新队伍
     * @param updateTeamRequest
     * @return
     */
    boolean updateById(UpdateTeamRequest updateTeamRequest, User loginUser);

    /**
     * 用户加入队伍
     * @param joinTeamRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(JoinTeamRequest joinTeamRequest , User loginUser);

    /**
     * 用户退出队伍
     * @param quitTeamRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(QuitTeamRequest quitTeamRequest, User loginUser);

    /**
     * 删除队伍 解散队伍
     * @param teamId
     * @param loginUser
     * @return
     */
    boolean deleteTeam(long teamId , User loginUser);
}
