package com.yhq.bishe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhq.bishe.model.domain.Price;
import com.yhq.bishe.model.dto.PriceQuery;

import java.util.List;

/**
* @author Henry
* @description 针对表【price(价目表)】的数据库操作Service
* @createDate 2023-03-31 17:20:33
*/
public interface PriceService extends IService<Price> {

    Long addPrice(Price price);

    List<Price> getPrice(PriceQuery priceQuery);

}
