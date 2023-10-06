package com.yhq.bishe.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yhq.bishe.common.BaseResponse;
import com.yhq.bishe.common.ErrorCode;
import com.yhq.bishe.common.ResultUtils;
import com.yhq.bishe.exception.BusinessException;
import com.yhq.bishe.mapper.UserMapper;
import com.yhq.bishe.model.domain.Booking;
import com.yhq.bishe.model.domain.Bookingtime;
import com.yhq.bishe.model.domain.Place;
import com.yhq.bishe.model.domain.User;
import com.yhq.bishe.model.domain.request.AddBookingRequest;
import com.yhq.bishe.model.domain.request.AddBookingTimeRequest;
import com.yhq.bishe.model.domain.request.PayBookingRequest;
import com.yhq.bishe.model.domain.request.UpdateBookingRequest;
import com.yhq.bishe.model.dto.BookingQuery;
import com.yhq.bishe.model.dto.BookingTimeQuery;
import com.yhq.bishe.model.vo.BookingPlaceUserVO;
import com.yhq.bishe.service.BookingService;
import com.yhq.bishe.service.BookingtimeService;
import com.yhq.bishe.service.PlaceService;
import com.yhq.bishe.service.UserService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.yhq.bishe.constant.UserConstant.SALT;

/**
 * @program: bisheBackend
 * @description: 用于处理订单相关的逻辑
 * @author: HenryYang
 * @create: 2023-03-30 08:16
 **/
@RestController
@RequestMapping("/booking")
@CrossOrigin(allowCredentials = "true", origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class BookingController {
    @Resource
    private UserService userService;
    @Resource
    private BookingService bookingService;
    @Resource
    private PlaceService placeService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private BookingtimeService bookingtimeService;

    /**
     * 新增加一个订单
     *
     * @param addBookingRequest
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addBooking(@RequestBody AddBookingRequest addBookingRequest, HttpServletRequest httpServletRequest) {
        if (addBookingRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        Booking booking = new Booking();
        try {
            Long userId = loginUser.getUserId();
            BeanUtils.copyProperties(booking, addBookingRequest);
            booking.setUserId(userId);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        long bookingId = bookingService.addBooking(booking);
        return ResultUtils.success(bookingId);
    }

    /**
     * 新接口：用于添加一个订单 同时添加已预订时间
     * @param addBookingRequest
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/add/list")
    public BaseResponse<Long> addBookingByList(@RequestBody AddBookingRequest addBookingRequest, HttpServletRequest httpServletRequest) {
        if (addBookingRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<AddBookingTimeRequest> addBookingTimeRequestList = addBookingRequest.getAddBookingTimeRequestList();
        if(CollectionUtils.isEmpty(addBookingTimeRequestList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        Booking booking = new Booking();
        try {
            Long userId = loginUser.getUserId();
            BeanUtils.copyProperties(booking, addBookingRequest);
            booking.setUserId(userId);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        long bookingId = bookingService.addBookingByList(booking, addBookingTimeRequestList);
//       执行添加addBooking逻辑
        boolean result = bookingtimeService.addBookingTime(addBookingTimeRequestList, bookingId);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        return ResultUtils.success(bookingId);
    }

    /**
     * 查询已预定的信息
     * @param bookingTimeQuery
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/list/booked")
    public BaseResponse<List<Bookingtime>> getBookedList(@RequestBody BookingTimeQuery bookingTimeQuery, HttpServletRequest httpServletRequest){
        if(bookingTimeQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userService.getLoginUser(httpServletRequest);
        QueryWrapper<Bookingtime> queryWrapper = new QueryWrapper<>();
        Long placeId = bookingTimeQuery.getPlaceId();
        if(placeId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        queryWrapper.eq("placeId",placeId);
        String sport = bookingTimeQuery.getSport();
        if(StringUtils.isBlank(sport)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        queryWrapper.eq("sport",sport);
        Integer type = bookingTimeQuery.getType();
        if(type == null || type > 1 || type < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        queryWrapper.eq("type",type);
        Date beginTime = bookingTimeQuery.getBeginTime();
        Date endTime = bookingTimeQuery.getEndTime();
        if(beginTime == null || endTime == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        queryWrapper.and(qw -> qw.ge("beginTime", beginTime));
        queryWrapper.and(qw -> qw.le("endTime", endTime));
        List<Bookingtime> list = bookingtimeService.list(queryWrapper);
        return ResultUtils.success(list);
    }
    /**
     * 取消订单
     *
     * @param updateBookingRequest
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/cancel")
    public BaseResponse<Boolean> cancelBooking(@RequestBody UpdateBookingRequest updateBookingRequest, HttpServletRequest httpServletRequest) {
//        todo 设计超时自动取消
        Long updateBookingId = preUpdateBooking(updateBookingRequest, httpServletRequest);
        if (updateBookingId == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改失败，请检查参数是否正确");
        }
        boolean result = bookingService.cancelBooking(updateBookingId);
        return ResultUtils.success(result);
    }

    /**
     * 支付订单
     *
     * @param payBookingRequest
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/pay")
    public BaseResponse<Boolean> payBooking(@RequestBody PayBookingRequest payBookingRequest, HttpServletRequest httpServletRequest) {
        Long updateBookingId = preUpdateBooking(payBookingRequest, httpServletRequest);
        if (updateBookingId == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改失败，请检查参数是否正确");
        }
        boolean result = bookingService.payBooking(updateBookingId);
        return ResultUtils.success(result);
    }

    /**
     * 已支付订单退款
     *
     * @param updateBookingRequest
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/refund")
    public BaseResponse<Boolean> refundBooking(@RequestBody UpdateBookingRequest updateBookingRequest, HttpServletRequest httpServletRequest) {
        Long updateBookingId = preUpdateBooking(updateBookingRequest, httpServletRequest);
        if (updateBookingId == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改失败，请检查参数是否正确");
        }
        boolean result = bookingService.refundBooking(updateBookingId);
        return ResultUtils.success(result);

    }
//    todo 增加改签业务

    /**
     * 条件查询所有订单
     *
     * @param bookingQuery
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<BookingPlaceUserVO>> listBooking(BookingQuery bookingQuery, HttpServletRequest httpServletRequest) {
        if (bookingQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        List<BookingPlaceUserVO> bookingPlaceUserVOList = new ArrayList<>();
        boolean isAdmin = userService.isAdmin(httpServletRequest);
        if (!isAdmin) {
            User loginUser = userService.getLoginUser(httpServletRequest);
            bookingQuery.setUserId(loginUser.getUserId());
        } else {
//            管理员可以查看所有用户的订单
            bookingQuery.setUserId(null);
        }
//        1.查询得到订单列表 bookingList
        List<Booking> bookingList = bookingService.listBooking(bookingQuery);

        for (Booking booking : bookingList) {
            BookingPlaceUserVO bookingPlaceUserVO = new BookingPlaceUserVO();
            try {
                BeanUtils.copyProperties(bookingPlaceUserVO, booking);
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "拷贝失败");
            }
            //        2，联合查询场地Place
            Long placeId = booking.getPlaceId();
            Place place = placeService.getById(placeId);
            Place safetyPlace = placeService.getSafetyPlace(place);
            bookingPlaceUserVO.setPlace(safetyPlace);
            //        3. 联合查询用户User
            Long userId = booking.getUserId();
            User user = userService.getById(userId);
            User safetyUser = userService.getSafetyUser(user);
            bookingPlaceUserVO.setCreateUser(safetyUser);
            bookingPlaceUserVOList.add(bookingPlaceUserVO);
        }

        return ResultUtils.success(bookingPlaceUserVOList);
    }

    @GetMapping("/list/my")
    public BaseResponse<List<BookingPlaceUserVO>> listMyBooking(BookingQuery bookingQuery, HttpServletRequest httpServletRequest) {
        return listBooking(bookingQuery, httpServletRequest);
    }

    /**
     * 修改订单信息前的逻辑 删除、退款、取消订单
     *
     * @param updateBookingRequest
     * @param httpServletRequest
     * @return
     */
    private Long preUpdateBooking(UpdateBookingRequest updateBookingRequest, HttpServletRequest httpServletRequest) {
        if (updateBookingRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long updateBookingId = updateBookingRequest.getBookingId();
        if (updateBookingId == null || updateBookingId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Booking booking = bookingService.getById(updateBookingId);
        if (booking == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Long userId = booking.getUserId();
        User loginUser = userService.getLoginUser(httpServletRequest);
        Long loginUserId = loginUser.getUserId();
        if (!userId.equals(loginUserId)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return updateBookingId;
    }

    /**
     * 函数重载
     * 修改订单信息前的逻辑 仅限支付时使用
     *
     * @param payBookingRequest
     * @param httpServletRequest
     * @return
     */
    private Long preUpdateBooking(PayBookingRequest payBookingRequest, HttpServletRequest httpServletRequest) {
//        判空
        if (payBookingRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        bookingId是否有效
        Long updateBookingId = payBookingRequest.getBookingId();
        if (updateBookingId == null || updateBookingId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        得到booking对象
        Booking booking = bookingService.getById(updateBookingId);
        if (booking == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
//        判断订单创建用户是否为当前用户
        Long userId = booking.getUserId();
        User loginUser = userService.getLoginUser(httpServletRequest);
        Long loginUserId = loginUser.getUserId();
        if (userId == null || !userId.equals(loginUserId)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
//        判断支付密码是否正确
        String userPassword = payBookingRequest.getUserPassword();
        if (StringUtils.isBlank(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
//        todo 需要修改 controller层不要加mapper 不符合mvc分成思想
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户密码不正确
        if (user == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "密码错误，支付失败");
        }
        return updateBookingId;
    }

}