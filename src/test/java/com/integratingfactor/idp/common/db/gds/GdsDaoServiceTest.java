package com.integratingfactor.idp.common.db.gds;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.integratingfactor.idp.common.exceptions.db.DbException;
import com.integratingfactor.idp.common.exceptions.db.NotFoundDbException;

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
    public void testGdsDaoServiceWriteReadByEntityKeyDeleteByEntityKey() throws DbException {
        Model wModel = testModel();
        dao.registerDaoEntity(TestDaoValueBySomethingByAnotherByKeyUtil.TestDaoValueBySomethingByAnotherByKeyPk.class);
        dao.registerDaoEntity(TestDaoValueBySomethingByAnotherByKeyUtil.TestDaoValueBySomethingByAnotherByKeyCk.class);
        dao.registerDaoEntity(TestDaoValueBySomethingByAnotherByKeyUtil.TestDaoValueBySomethingByAnotherByKey.class);
        dao.save(TestDaoValueBySomethingByAnotherByKeyUtil.toEntity(wModel));
        Model rModel = TestDaoValueBySomethingByAnotherByKeyUtil
                .toModel(dao.readByEntityKey(TestDaoValueBySomethingByAnotherByKeyUtil.toKey(testModel())));
        Assert.assertEquals(rModel.value, wModel.value);

        dao.delete(TestDaoValueBySomethingByAnotherByKeyUtil.toKey(rModel));
        try {
            dao.readByEntityKey(TestDaoValueBySomethingByAnotherByKeyUtil.toKey(rModel));
            Assert.fail("data still exists after calling delete");
        } catch (NotFoundDbException e) {
            System.out.println("Success: " + e.getError());
        }

    }

}
