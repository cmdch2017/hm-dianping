package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ShopTypeMapper shopTypeMapper;

    @Override
    public List<ShopType> queryAllType() throws JsonProcessingException {
        //从缓存中查数据
        String shopTypeJson = stringRedisTemplate.opsForValue().get("cache:shopType");
        //如果缓存命中，
        if (StrUtil.isNotBlank(shopTypeJson)) {
            return new ObjectMapper().readValue(shopTypeJson, new TypeReference<List<ShopType>>() {
            });
        }
        //如果缓存不命中，查数据库
        List<ShopType> shopTypeList = shopTypeMapper.selectList(null).stream().sorted(Comparator.comparingInt(ShopType::getSort))
                .collect(Collectors.toList());
        if (shopTypeList == null) {
            return null;
        } else {
            stringRedisTemplate.opsForValue().set("cache:shopType", new ObjectMapper().writeValueAsString(shopTypeList));
        }
        //如果过数据库不命中，则返回错误
        //如果过数据库命中，则更新缓存
        return null;
    }
}
