package io.vertx.codetrans;

import io.vertx.codetrans.lang.groovy.GroovyLang;
import io.vertx.codetrans.lang.kotlin.KotlinLang;
import io.vertx.codetrans.lang.scala.ScalaLang;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class VariableTest extends ConversionTestBase {

  public static boolean cond;
  public static final String constant = "foo";
  public static Object o;

  @Before
  public void before() {
    o = null;
  }

  @Test
  public void testDeclareVariable() throws Exception {
    runAll("variable/Variable", "declare", () -> {
      Assert.assertEquals("foo", o);
      o = null;
    });
  }

  @Test
  public void testDeclareAndAssignNull() throws Exception {
    cond = true;
    runAllExcept("variable/Variable", "declareAndAssignNull", ScalaLang.class, () -> {
      Assert.assertEquals("lazy", o);
      o = null;
    });
    o = "foo";
    cond = false;
    runAllExcept("variable/Variable", "declareAndAssignNull", ScalaLang.class, () -> {
      Assert.assertEquals(null, o);
      o = "foo";
    });
  }

  @Test
  public void testGlobalExpression() throws Exception {
    runAll("variable/Variable", "globalExpression", Collections.singletonMap("vertx", "vertx_object"), Arrays.asList(KotlinLang.class, ScalaLang.class), () -> {
      Assert.assertEquals("vertx_object", o);
      o = null;
    });
  }

  @Test
  public void testMemberExpression() throws Exception {
    runAll("variable/Variable", "memberExpression", () -> {
      Assert.assertEquals("member_value", o);
      o = null;
    });
  }

  @Test
  public void testUninitializedMemberExpression() throws Exception {
    run(new GroovyLang(), "variable/Variable", "uninitializedMemberExpression");
    Assert.assertNull(o);
    o = null;
    // kotlin doesn't allow uninitialized member access
//    run(new KotlinLang(), "variable/Variable", "uninitializedMemberExpression");
//    Assert.assertNull(o);
//    o = null;
    run(new ScalaLang(), "variable/Variable", "uninitializedMemberExpression");
    Assert.assertNull(o);
    o = null;
  }

  @Test
  public void testMemberExpressionAccessedByMethod() throws Exception {
    runAll("variable/Variable", "memberExpressionAccessedByMethod", () -> {
      Assert.assertEquals("member_value", o);
      o = null;
    });
  }
}
