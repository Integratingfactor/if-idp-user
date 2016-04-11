package com.integratingfactor.idp.common.db.gds;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.integratingfactor.idp.common.exceptions.db.DbException;

@ContextConfiguration(classes = { GdsDaoServiceTestConfig.class })
public class GdsDaoServiceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    GdsDaoService dao;

    public static class Model {
        String key;

        String another;

        String something;

        String value;
    }

    public static final String TestKey = "test-key";

    public static final String TestAnother = "another-ck";

    public static final String TestSomething = "something-ck";

    public static final String TestValue = "this is the value by something by another by key";

    public static Model testModel() {
        Model model = new Model();
        model.key = TestKey;
        model.another = TestAnother;
        model.something = TestSomething;
        model.value = TestValue;
        return model;
    }

    @Test
    public void testGdsDaoService() throws DbException {
        dao.registerDaoEntity(TestDaoValueBySomethingByAnotherByKeyUtil.TestDaoValueBySomethingByAnotherByKeyPk.class);
        dao.registerDaoEntity(TestDaoValueBySomethingByAnotherByKeyUtil.TestDaoValueBySomethingByAnotherByKeyCk.class);
        dao.registerDaoEntity(TestDaoValueBySomethingByAnotherByKeyUtil.TestDaoValueBySomethingByAnotherByKey.class);
        dao.save(TestDaoValueBySomethingByAnotherByKeyUtil.toEntity(testModel()));
    }

}
