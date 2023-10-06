package com.yhq.bishe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhq.bishe.model.domain.Bookingtime;
import com.yhq.bishe.model.domain.request.AddBookingRequest;
import com.yhq.bishe.model.domain.request.AddBookingTimeRequest;

import java.util.List;

/**
* @author Henry
* @description 针对表【bookingtime(已预订时间表)】的数据库操作Service
* @createDate 2023-04-04 08:42:57
*/
public interface BookingtimeService extends IService<Bookingtime> {
    boolean addBookingTime(List<AddBookingTimeRequest> addBookingTimeRequestList, Long bookingId);
    boolean deleteBookingTime(Long bookingId);
}
