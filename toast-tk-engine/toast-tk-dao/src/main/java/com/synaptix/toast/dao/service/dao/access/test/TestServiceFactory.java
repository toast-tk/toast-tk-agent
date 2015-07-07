package com.synaptix.toast.dao.service.dao.access.test;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.dao.domain.impl.test.block.CommentBlock;
import com.synaptix.toast.dao.domain.impl.test.block.ConfigBlock;
import com.synaptix.toast.dao.domain.impl.test.block.InsertBlock;
import com.synaptix.toast.dao.domain.impl.test.block.SetupBlock;
import com.synaptix.toast.dao.domain.impl.test.block.TestBlock;
import com.synaptix.toast.dao.service.dao.common.AbstractMongoDaoService;

//to be used later when we'll need to create templates
@Singleton
@Deprecated
public class TestServiceFactory {

	private final CommentBlockDaoService commentService;

	private final TestPageDaoService testPageService;

	private final ConfigBlockDaoService configService;

	private final InsertBlockDaoService insertService;

	private final SetupBlockDaoService setupService;

	private final TestBlockDaoService tsService;

	private final Map<Class<?>, AbstractMongoDaoService<?>> map;

	@Inject
	public TestServiceFactory(
		CommentBlockDaoService.Factory bFactory,
		TestPageDaoService.Factory tFactory,
		ConfigBlockDaoService.Factory cFactory,
		InsertBlockDaoService.Factory iFactory,
		SetupBlockDaoService.Factory sFactory,
		TestBlockDaoService.Factory tsFactory) {
		this.commentService = bFactory.create(null);
		this.testPageService = tFactory.create(null);
		this.configService = cFactory.create(null);
		this.insertService = iFactory.create(null);
		this.setupService = sFactory.create(null);
		this.tsService = tsFactory.create(null);
		map = new HashMap<Class<?>, AbstractMongoDaoService<?>>();
		map.put(CommentBlock.class, commentService);
		map.put(ConfigBlock.class, configService);
		map.put(InsertBlock.class, insertService);
		map.put(SetupBlock.class, setupService);
		map.put(TestBlock.class, tsService);
		map.put(TestPage.class, testPageService);
	}

	public void saveEntity(
		IBlock entity) {
		if(map.get(entity.getClass()) != null) {
			if(CommentBlock.class.equals(entity.getClass())) {
				commentService.save((CommentBlock) entity);
			}
			else if(ConfigBlock.class.equals(entity.getClass())) {
				configService.save((ConfigBlock) entity);
			}
			else if(InsertBlock.class.equals(entity.getClass())) {
				insertService.save((InsertBlock) entity);
			}
			else if(SetupBlock.class.equals(entity.getClass())) {
				setupService.save((SetupBlock) entity);
			}
			else if(TestBlock.class.equals(entity.getClass())) {
				tsService.save((TestBlock) entity);
			}
			else if(TestPage.class.equals(entity.getClass())) {
				testPageService.save((TestPage) entity);
			}
		}
		else {
			throw new IllegalAccessError("entity not covered by service: " + entity.getClass());
		}
	}
}
