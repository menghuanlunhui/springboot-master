package com.jf.mapper;

import com.jf.database.model.custom.BaseVo;
import com.jf.database.model.manage.Address;

import java.util.List;

/**
 * AddressMapper Interface
 * @date 2017年05月09日 上午 11:06:55
 * @Author admin
 */
public interface AddressMapper {

	List<Address> findByCondition(BaseVo baseVo);

	int insert(Address bean);

	int update(Address bean);

	int delete(Integer id);

}