package com.yhq.bishe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhq.bishe.model.domain.Booking;
import com.yhq.bishe.model.domain.request.AddBookingTimeRequest;
import com.yhq.bishe.model.dto.BookingQuery;

import java.util.List;

/**
* @author Henry
* @description 针对表【booking(订单)】的数据库操作Service
* @createDate 2023-03-30 08:07:30
*/
public interface BookingService extends IService<Booking> {

    List<Booking> listBooking(BookingQuery bookingQuery);

    long addBooking(Booking booking);
    long addBookingByList(Booking booking, List<AddBookingTimeRequest> addBookingTimeRequestList);

    boolean cancelBooking(Long cancelBookingId);

    boolean payBooking(Long payBookingId);

    boolean refundBooking(Long refundBookingId);
}
