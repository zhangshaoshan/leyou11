package com.leyou.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.enums.ExceptionEnum;
import com.leyou.exception.LyException;
import com.leyou.mapper.BrandMapper;
import com.leyou.pojo.Brand;
import com.leyou.vo.PageResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandByPage(Integer page,Integer rows,String sortBy,Boolean desc,String key){
        //分页  分页助手  自动拦截sql语句 拼上limit
        PageHelper.startPage(page,rows);
        /**
         * SELECT * FROM tb_brand WHERE `name` LIKE '%hx%' OR letter='H' ORDER BY id DESC
         * */
        Example example = new Example(Brand.class);
        //过滤
        if (StringUtils.isNotBlank(key)){
            example.createCriteria().orLike("name","%+key+%").orEqualTo("letter",key.toUpperCase());
        }
        //排序
        if (StringUtils.isNotBlank(sortBy)){
            String orderByClause = sortBy + (desc?" DESC":" ASC");
            example.setOrderByClause(orderByClause);
        }
        //查询
        List<Brand> list = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //分页助手获取总条数，当前页
        PageInfo<Brand> info = new PageInfo<Brand>(list);
        return new PageResult<Brand>(info.getTotal(),info.getPages(),list);
    }

    @Transactional
    public void createBrand(Brand brand, List<Long> cids) {
        //新增品牌
        brand.setId(null);
        int insert = brandMapper.insert(brand);
        if (insert == 0){
            throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
        }
        //新增中间表
        for (Long cid:
             cids) {
            int row = brandMapper.insertCategoryBrand(cid, brand.getId());
            if (row == 0){
                throw  new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
            }
        }
    }

    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> brands = brandMapper.queryBrandByCid(cid);
        if (CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brands;
    }

    /**
     * 根据品牌id查询品牌
     * */
    public Brand queryBrandByBrandId(Long brandId){
        Brand brand = brandMapper.selectByPrimaryKey(brandId);
        if (brand == null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }
}
