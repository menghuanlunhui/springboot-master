package com.jf.database.mapper;

import com.jf.database.model.User;
import com.jf.database.model.custom.BaseVo;
import com.jf.database.model.custom.IdText;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Test Interface
 *
 * @Author admin
 * @date 2017年05月09日 上午 11:06:55
 */
public interface TestMapper {

	List<User> findByCondition(BaseVo baseVo);

	User findById(Integer id);

	User findSimpleById(Integer id);

	int insert(User bean);

	int update(User bean);


	// 测试Mybatis typeHandler
	User findUserById(Integer id);

	User findUserById2(Integer id);

	User findUserById3(Integer id);

	int insertUser(User user);

	int insertUser2(User user);

	int insertUser3(User user);

	int insert1(@Param("name") String name, @Param("age") Integer age);

	int insert2(@Param("name") String name, @Param("age") Integer age);

}