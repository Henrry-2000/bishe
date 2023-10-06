package com.yhq.bishe.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhq.bishe.model.domain.UserTeam;
import com.yhq.bishe.service.UserTeamService;
import com.yhq.bishe.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author Henry
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-03-19 16:41:16
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService {

}




