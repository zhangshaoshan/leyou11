package com.leyou.service;

import com.leyou.enums.ExceptionEnum;
import com.leyou.exception.LyException;
import com.leyou.mapper.SpecGroupMapper;
import com.leyou.mapper.SpecParamMapper;
import com.leyou.pojo.SpecGroup;
import com.leyou.pojo.SpecParam;
import com.leyou.vo.ResponseModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpecificationService {
    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    public List<SpecGroup> queryGroupByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> list = specGroupMapper.select(specGroup);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        return list;
    }

    /**
     * 新增  删除   修改
     * */
    public Long addGroup(SpecGroup specGroup) {
        specGroup.setId(null);
        int row = specGroupMapper.insert(specGroup);
        if (row == 0){
            throw new LyException(ExceptionEnum.INSERT_SPEC_GROUP_FALL);
        }
        return specGroup.getId();
    }


    public List<SpecParam> queryParamList(Long groupId,Long cid,Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(groupId);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        List<SpecParam> list = specParamMapper.select(specParam);  //如果三个都是null  因为是根据非空参数来查，所以会报not found错误
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return list;
    }

    public Long addSpecParam(SpecParam specParam) {
        if (CollectionUtils.isEmpty(specParam.getSegmentList()) == false ){
//            String segments = "";
//            for (String segment:
//                    specParam.getSegmentList()) {
//                segments += (segment+",");
//            }
//            segments = segments.substring(0,segments.length()-1);
//            specParam.setSegments(segments);
            String segments = StringUtils.join(specParam.getSegmentList(),",");
            specParam.setSegments(segments);
        }
        int insert = specParamMapper.insertSelective(specParam);
        if (insert == 0){
            throw new LyException(ExceptionEnum.SPEC_PARAM_INSERT_FALL);
        }
        return specParam.getId();
    }

    public Long editParam(SpecParam specParam) {
        if (specParam.getId() == null){
            throw new LyException(ExceptionEnum.SPEC_PARAM_EDIT_ID_NULL_FALL);
        }
        int row = specParamMapper.updateByPrimaryKeySelective(specParam);
        if (row == 0){
            throw new LyException(ExceptionEnum.SPEC_PARAM_EDIT_FALL);
        }
        return specParam.getId();
    }


    public List<SpecGroup> querySpecGroupByCid(Long cid) {
        List<SpecGroup> specGroupList = queryGroupByCid(cid);
        //查询当前分类下的所有参数
        List<SpecParam> specParamList = queryParamList(null,cid,null);
        //将 specParamList变成map
        Map<Long,List<SpecParam>> specParamMap = new HashMap<>();
        for (SpecParam specParam:
             specParamList) {
            if (!specParamMap.containsKey(specParam.getGroupId())){
                specParamMap.put(specParam.getGroupId(),new ArrayList<SpecParam>());
            }
            specParamMap.get(specParam.getGroupId()).add(specParam);
        }
        //将param 填充到 group
        for (SpecGroup specGroup:
             specGroupList) {
            specGroup.setSpecParamList(specParamMap.get(specGroup.getId()));
        }
        //
        return specGroupList;
    }
}
