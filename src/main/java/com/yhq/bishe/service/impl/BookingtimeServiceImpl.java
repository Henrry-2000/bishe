package com.yhq.bishe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhq.bishe.common.ErrorCode;
import com.yhq.bishe.exception.BusinessException;
import com.yhq.bishe.model.domain.Bookingtime;
import com.yhq.bishe.mapper.BookingtimeMapper;
import com.yhq.bishe.model.domain.request.AddBookingRequest;
import com.yhq.bishe.model.domain.request.AddBookingTimeRequest;
import com.yhq.bishe.service.BookingtimeService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
* @author Henry
* @description 针对表【bookingtime(已预订时间表)】的数据库操作Service实现
* @createDate 2023-04-04 08:42:57
*/
@Service
public class BookingtimeServiceImpl extends ServiceImpl<BookingtimeMapper, Bookingtime>
    implements BookingtimeService{

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addBookingTime(List<AddBookingTimeRequest> addBookingTimeRequestList, Long bookingId) {
        if(CollectionUtils.isEmpty(addBookingTimeRequestList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(bookingId == null || bookingId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        for (AddBookingTimeRequest addBookingTimeRequest:
             addBookingTimeRequestList) {
            Bookingtime bookingtime = new Bookingtime();
            try {
                BeanUtils.copyProperties(bookingtime,addBookingTimeRequest);
                bookingtime.setBookingId(bookingId);
            }catch (Exception e){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
            boolean save = this.save(bookingtime);
            if(!save){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
        return true;
    }

    @Override
    public boolean deleteBookingTime(Long bookingId) {
        if(bookingId == null || bookingId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Bookingtime> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bookingId",bookingId);
        boolean remove = this.remove(queryWrapper);
        return remove;
    }
}




