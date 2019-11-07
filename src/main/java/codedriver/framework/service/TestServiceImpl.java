package codedriver.framework.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import codedriver.framework.dao.mapper.TestMapper;

@Service
public class TestServiceImpl implements TestService {

	@Autowired
	private TestMapper testMapper;

	@Override
	public int updateTest(String newName) {
		testMapper.updateTest(newName);
		Integer.parseInt("aa");
		return 1;
	}

}
