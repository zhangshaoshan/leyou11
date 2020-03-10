package com.leyou.service;

import com.leyou.enums.ExceptionEnum;
import com.leyou.exception.LyException;
import com.leyou.mapper.CategoryMapper;
import com.leyou.pojo.Category;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> queryCategoryListByPid(Long pid){
        Category category = new Category();
        category.setParentId(pid);
        List<Category> categoryList = categoryMapper.select(category);
        if (CollectionUtils.isEmpty(categoryList)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return categoryList;
    }

    public Long insertCategory(Category category){
       int row = categoryMapper.insert(category);
        if (row == 0){
            throw new LyException(ExceptionEnum.CATEGORY_INSERT_FAIL);
        }
        return category.getId();
    }

    public Long updateCategory(Category category){
        if (category.getId() == null){
            throw new LyException(ExceptionEnum.PARAM_FALL);
        }
        int row = categoryMapper.updateByPrimaryKeySelective(category);
        if (row == 0){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return category.getId();
    }
        /**
         * 查询分类s
         * */
    public List<Category> queryCategoryListByIds(List<Long> cids){
        List<Category> categoryList = categoryMapper.selectByIdList(cids);
        if (categoryList == null){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return categoryList;
    }



}
