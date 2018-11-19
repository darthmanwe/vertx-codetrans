package io.vertx.codetrans.expression;

import io.vertx.codegen.type.TypeInfo;
import io.vertx.codetrans.CodeBuilder;
import io.vertx.codetrans.CodeWriter;
import io.vertx.codetrans.MethodSignature;
import io.vertx.codetrans.TypeArg;

import java.util.List;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class DataObjectModel extends ExpressionModel {

  final ExpressionModel expression;

  public DataObjectModel(CodeBuilder builder, ExpressionModel expression) {
    super(builder);
    this.expression = expression;
  }

  @Override
  public ExpressionModel onMethodInvocation(TypeInfo receiverType, MethodSignature method, TypeInfo returnType, List<TypeArg> typeArguments, List<ExpressionModel> argumentModels, List<TypeInfo> argumentTypes) {
    String methodName = method.getName();
    if (DataObjectLiteralModel.isSet(methodName)) {
      return builder.render(writer -> {
        writer.renderDataObjectAssign(expression,
            DataObjectLiteralModel.unwrapSet(methodName),
            argumentModels.get(0).toDataObjectValue());
      });
    }
    if (DataObjectLiteralModel.isGet(methodName)) {
      return builder.render(writer -> {
        writer.renderDataObjectMemberSelect(expression,
            DataObjectLiteralModel.unwrapSet(methodName));
      });
    }
    if (isToJson(method)) {
      return builder.render(writer -> {
        writer.renderDataObjectToJson((IdentifierModel) expression);
      });
    }
    throw new UnsupportedOperationException("Unsupported method " + method + " on object model");
  }

  private boolean isToJson(MethodSignature method) {
    return "toJson".equals(method.getName())
      && method.getReturnType().getKind().json
      && method.getParameterTypes().isEmpty();
  }

  @Override
  public void render(CodeWriter writer) {
    expression.render(writer);
  }
}
