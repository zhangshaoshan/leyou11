package com.leyou.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.dto.CartDTO;
import com.leyou.enums.ExceptionEnum;
import com.leyou.exception.LyException;
import com.leyou.mapper.SkuMapper;
import com.leyou.mapper.SpuDetailMapper;
import com.leyou.mapper.SpuMapper;
import com.leyou.mapper.StockMapper;
import com.leyou.pojo.*;
import com.leyou.vo.PageResult;
import com.netflix.discovery.converters.Auto;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 分页获取商品通用参数
     * */
    public PageResult<Spu> querySpuPage(Integer page, Integer rows, Boolean saleable, String key){
        //分页  分页助手  自动拦截sql语句 拼上limit
        PageHelper.startPage(page,rows);
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //设置过滤条件
        //上下架
        if (saleable != null){
            criteria.orEqualTo("saleable",saleable);
        }
        //根据名称模糊查询
        if (StringUtils.isEmpty(key) == false){
            criteria.andLike("title","%"+key+"%");
        }
        //默认排序
        example.setOrderByClause("last_update_time DESC");
        //查询
        List<Spu> spuList = spuMapper.selectByExample(example);
        //获取分类名称  前端用
        loadCategoryAndBrandName(spuList);
        PageInfo<Spu> pageInfo = new PageInfo<>(spuList);
        return new PageResult<>(pageInfo.getTotal(),spuList);
    }

    private void loadCategoryAndBrandName(List<Spu> spus){
        for (Spu spu:
             spus) {
            configSpuCategoryName(spu);
        }
    }

    /**
     * 新增商品
     * */
    @Transactional
    public void addGoods(Spu spu) {
        //新增spu
        int row = spuMapper.insertSelective(spu);
        if (row != 1){
            throw new LyException(ExceptionEnum.GOODS_INSERT_FALL);
        }
        //新增spuDetail
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        int spuDetailRow = spuDetailMapper.insertSelective(spuDetail);
        if (spuDetailRow != 1){
            throw new LyException(ExceptionEnum.GOODS_INSERT_FALL);
        }
        //新增sku  //新增库存
        addSkuAndStock(spu);
        //发送消息
        amqpTemplate.convertAndSend("item.insert",spu.getId());//这里使用了yaml里面默认的交换机名称
    }

    /**
     * 获取商品detail
     * */
    public SpuDetail querySpuDetailBySpuId(Long spuId){
       SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spuId);
       if (spuDetail == null){
           throw new LyException(ExceptionEnum.SPUDETAIL_NOT_FOUND);
       }
        return spuDetail;
    }

    /**
     * 获取商品sku
     * */
    public List<Sku> querySkuBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skuList = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skuList)){
            throw new LyException(ExceptionEnum.SKU_NOT_FOUND);
        }
//        //查询库存
//        for (Sku tagSku:
//             skuList) {
//            Stock stock = stockMapper.selectByPrimaryKey(tagSku.getId());
//            if (stock == null){
//                throw new LyException(ExceptionEnum.STOCK_NOT_FOUND);
//            }
//            tagSku.setStock(stock);
//        }
        querySkuStock(skuList);
        return skuList;
    }

    private void querySkuStock(List<Sku> skuList) {
        List<Long> skuIds = skuList.stream().map(Sku::getId).collect(Collectors.toList());
        List<Stock> stocks = stockMapper.selectByIdList(skuIds);
        if (CollectionUtils.isEmpty(stocks)){
            throw new LyException(ExceptionEnum.STOCK_NOT_FOUND);
        }
        Map<Long,Long> stockMap = stocks.stream().collect(Collectors.toMap(Stock::getSkuId,Stock::getStock));
        skuList.forEach(tagSku->tagSku.setStock(stockMap.get(tagSku.getId())));
    }

    /**
     * 更新商品
     * */
    @Transactional
    public Spu updateGoods(Spu spu) {
        //查找出sku
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        List<Sku> skuList = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skuList) == false){
            //删除原sku
            List<Long> skuIdList = skuList.stream().map(Sku::getId).collect(Collectors.toList());
            skuMapper.deleteByIdList(skuIdList);
            //删除库存
            stockMapper.deleteByIdList(skuIdList);
        }
        //修改spu
        Date lastUpdateTime = new Date();
        spu.setLastUpdateTime(lastUpdateTime);
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count != 1){
            throw new LyException(ExceptionEnum.UPDATE_SPU_FAIL);
        }
        //修改spuDetail
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        spuDetailMapper.updateByPrimaryKeySelective(spuDetail);
        //新增sku 新增库存
        addSkuAndStock(spu);
        //发送消息
        amqpTemplate.convertAndSend("item.update",spu.getId());//这里使用了yaml里面默认的交换机名称
        return spu;
    }


    private void addSkuAndStock(Spu spu){
        //新增sku
        List<Sku> skuList = spu.getSkus();
        List<Stock> stockList = new ArrayList<>();
        for (Sku sku:
                skuList) {
            sku.setSpuId(spu.getId());
            int skuRow = skuMapper.insertSelective(sku);
            if (skuRow != 1){
                throw new LyException(ExceptionEnum.GOODS_INSERT_FALL);
            }
            Stock stock = new Stock();
            Long count = sku.getStock();
            stock.setSkuId(sku.getId());
            stock.setStock(count);
            stockList.add(stock);
        }
        //新增库存
        int stockRow = stockMapper.insertList(stockList);
        if (stockRow == 0){
            throw new LyException(ExceptionEnum.GOODS_INSERT_FALL);
        }
    }

    /**
    *  根据spuid 查询spu
    * */
    public Spu querySpuBySpuId(Long spuId){
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if (spu == null){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //处理分类名称
        configSpuCategoryName(spu);
        //同时查询sku
        List<Sku> skuList = querySkuBySpuId(spuId);
        spu.setSkus(skuList);
        //同时查询spuDetail
        SpuDetail spuDetail = querySpuDetailBySpuId(spuId);
        spu.setSpuDetail(spuDetail);
        return spu;
    }

    /**
     * 查询spu分类，，并且处理分类名称
     * */
    private void configSpuCategoryName(Spu spu){
        List<Category> categoryList = categoryService.queryCategoryListByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
        List<String> categoryNames = categoryList.stream().map(Category::getName).collect(Collectors.toList());
        String categoryNamesString = org.apache.commons.lang3.StringUtils.join(categoryNames,"/");
        spu.setCategoryName(categoryNamesString);
    }

    public List<Sku> querySkuListBySkuIds(List<Long> ids) {
        List<Sku> skuList = skuMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(skuList)){
            throw new LyException(ExceptionEnum.SKU_NOT_FOUND);
        }
        querySkuStock(skuList);
        return skuList;

    }

    @Transactional
    public void decreaseStockByCarts(List<CartDTO> cartDTOs) {
        for (CartDTO cartDTO:
             cartDTOs) {
            int count = stockMapper.decreaseStock(cartDTO.getSkuId(),cartDTO.getNum());
            if (count != 1){
                throw new LyException(ExceptionEnum.STOCK_ON_ENOUGH);
            }
        }
    }
}
