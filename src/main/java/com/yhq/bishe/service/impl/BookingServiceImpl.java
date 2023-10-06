package com.yhq.bishe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhq.bishe.common.ErrorCode;
import com.yhq.bishe.exception.BusinessException;
import com.yhq.bishe.model.domain.Booking;
import com.yhq.bishe.mapper.BookingMapper;
import com.yhq.bishe.model.domain.Place;
import com.yhq.bishe.model.domain.Price;
import com.yhq.bishe.model.domain.request.AddBookingTimeRequest;
import com.yhq.bishe.model.dto.BookingQuery;
import com.yhq.bishe.model.dto.PriceQuery;
import com.yhq.bishe.service.BookingService;
import com.yhq.bishe.constant.BookingConstant;
import com.yhq.bishe.service.BookingtimeService;
import com.yhq.bishe.service.PlaceService;
import com.yhq.bishe.service.PriceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BookingServiceImpl extends ServiceImpl<BookingMapper, Booking>
    implements BookingService{
    @Resource
    private PlaceService placeService;
    @Resource
    private PriceService priceService;
    @Resource
    private BookingtimeService bookingtimeService;
    @Override
    public List<Booking> listBooking(BookingQuery bookingQuery) {
//        1.按条件查询
        QueryWrapper<Booking> bookingQueryWrapper = new QueryWrapper<>();
        Long bookingId = bookingQuery.getBookingId();
        if(bookingId != null){
            bookingQueryWrapper.eq("bookingId",bookingId);
        }
        Integer bookingStatus = bookingQuery.getBookingStatus();
        if(bookingStatus != null){
            bookingQueryWrapper.eq("bookingStatus",bookingStatus);
        }
        Integer type = bookingQuery.getType();
        if(type != null){
            bookingQueryWrapper.eq("type",type);
        }

        BigDecimal fee = bookingQuery.getFee();
        if(fee != null){
            bookingQueryWrapper.eq("fee",fee);
        }
        Long placeId = bookingQuery.getPlaceId();
        if(placeId != null){
            bookingQueryWrapper.eq("placeId",placeId);
        }
        Long userId = bookingQuery.getUserId();
        if(userId != null){
            bookingQueryWrapper.eq("userId",userId);
        }
        Date beginTime = bookingQuery.getBeginTime();
        if(beginTime != null){
            bookingQueryWrapper.eq("beginTime",beginTime);
        }
        Date endTime = bookingQuery.getEndTime();
        if(endTime != null){
            bookingQueryWrapper.eq("endTime",endTime);
        }
        List<Booking> bookingList = new ArrayList<Booking>();
        try {
            bookingList = this.list(bookingQueryWrapper);
        }
        catch (Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        return bookingList;
    }

    @Override
    public long addBooking(Booking booking) {
        if(booking == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //查找场地是否存在
        Long placeId = booking.getPlaceId();
        Place place = placeService.getById(placeId);
        if (place == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        //计算金额是否正确
        Date beginTime = booking.getBeginTime();
        Date endTime = booking.getEndTime();
        if(beginTime == null || endTime == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Instant beginInstant = beginTime.toInstant();
        Instant endInstant = endTime.toInstant();
        long seconds = Duration.between(beginInstant, endInstant).getSeconds();
        long hours = seconds/3600;
        PriceQuery priceQuery = new PriceQuery();
        priceQuery.setPlaceId(placeId);
        priceQuery.setType(booking.getType());
        priceQuery.setSport(booking.getSport());
        priceQuery.setBeginTime(booking.getBeginTime());
        priceQuery.setEndTime(booking.getEndTime());
        List<Price> priceList = priceService.getPrice(priceQuery);
        //查询单价
        BigDecimal unitPrice;
        if(CollectionUtils.isEmpty(priceList)){
            throw new BusinessException(ErrorCode.NULL_ERROR,"后台查询价目表为空，无法创建订单");
        }
        else if(priceList.size() == 1){
            Price price = priceList.get(0);
            unitPrice = price.getUnitPrice();
        }
        else{
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"目前还未实现单价不一致的订单的支付逻辑");
        }
        BigDecimal duration = new BigDecimal(hours);
        BigDecimal totalPrice = unitPrice.multiply(duration);
        BigDecimal fee = booking.getFee();
        if(totalPrice != null && fee != null){
            int flag = totalPrice.compareTo(fee);
            if(flag != 0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"总价不正确");
            }
        }
        //设置为未支付
        booking.setBookingStatus(BookingConstant.WAITING);
        boolean result = this.save(booking);
        if(!result){
            return -1;
        }
        Long bookingId = booking.getBookingId();
        if(bookingId == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"创建订单失败");
        }

        return bookingId;

    }

    /**
     * 创建一个booking
     *
     * @param booking
     * @param addBookingTimeRequestList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addBookingByList(Booking booking, List<AddBookingTimeRequest> addBookingTimeRequestList) {
        if(booking == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //查找场地是否存在
        Long placeId = booking.getPlaceId();
        if(placeId == null || placeId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Place place = placeService.getById(placeId);
        if (place == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        //计算总金额是否正确
        BigDecimal totalPrice = BigDecimal.valueOf(0);
        for (AddBookingTimeRequest addBookingTimeRequest:
                addBookingTimeRequestList) {

            Date beginTime = addBookingTimeRequest.getBeginTime();
            Date endTime = addBookingTimeRequest.getEndTime();
            if(beginTime == null || endTime == null){
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
//          设置bookingTimeRequest的参数,以便addBookingTime
            addBookingTimeRequest.setType(booking.getType());
            addBookingTimeRequest.setSport(booking.getSport());
            addBookingTimeRequest.setPlaceId(booking.getPlaceId());
//          设置queryWrapper来查询单价表
//           查询单价
            PriceQuery priceQuery = new PriceQuery();
            priceQuery.setPlaceId(placeId);
            priceQuery.setType(booking.getType());
            priceQuery.setSport(booking.getSport());
            priceQuery.setCourtName(addBookingTimeRequest.getCourtName());
            priceQuery.setBeginTime(beginTime);
            priceQuery.setEndTime(endTime);
            List<Price> priceList = priceService.getPrice(priceQuery);
            BigDecimal unitPrice;
            if(CollectionUtils.isEmpty(priceList)){
                throw new BusinessException(ErrorCode.NULL_ERROR,"后台查询价目表为空，无法创建订单");
            }
            else if(priceList.size() == 1){
                Price price = priceList.get(0);
                unitPrice = price.getUnitPrice();
                totalPrice = totalPrice.add(unitPrice);
            }
            else{
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"目前还未实现单价不一致的订单的支付逻辑");
            }
        }
        BigDecimal fee = booking.getFee();
        if(totalPrice != null && fee != null){
            int flag = totalPrice.compareTo(fee);
            if(flag != 0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"总价不正确");
            }
        }
        //设置为未支付
        booking.setBookingStatus(BookingConstant.WAITING);
        boolean result = this.save(booking);
        if(!result){
            return -1;
        }
        Long bookingId = booking.getBookingId();
        if(bookingId == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"创建订单失败");
        }
        bookingtimeService.addBookingTime(addBookingTimeRequestList,bookingId);
        return bookingId;
    }

    @Override
    public boolean cancelBooking(Long cancelBookingId) {
        Booking booking = this.getById(cancelBookingId);
        Integer oldStatus = booking.getBookingStatus();
        if(!oldStatus.equals(BookingConstant.WAITING)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"订单状态异常，该订单无法取消");
        }
        booking.setBookingStatus(BookingConstant.CANCEL);
        boolean result = bookingtimeService.deleteBookingTime(cancelBookingId);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return this.updateById(booking);

    }

    @Override
    public boolean payBooking(Long payBookingId) {
        Booking booking = this.getById(payBookingId);
        Integer oldStatus = booking.getBookingStatus();
        if(!oldStatus.equals(BookingConstant.WAITING)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"订单状态异常，该订单无法支付");
        }
        booking.setBookingStatus(BookingConstant.PAID);
        return this.updateById(booking);
    }
    @Override
    public boolean refundBooking(Long refundBookingId) {
        Booking booking = this.getById(refundBookingId);
        Integer oldStatus = booking.getBookingStatus();
        if(!oldStatus.equals(BookingConstant.PAID)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"订单状态异常，该订单无法退款");
        }
        booking.setBookingStatus(BookingConstant.REFUND);
        boolean result = bookingtimeService.deleteBookingTime(refundBookingId);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return this.updateById(booking);
    }
}




