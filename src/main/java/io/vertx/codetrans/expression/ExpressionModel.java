package io.vertx.codetrans.expression;

import io.vertx.codegen.type.TypeInfo;
import io.vertx.codetrans.CodeBuilder;
import io.vertx.codetrans.CodeModel;
import io.vertx.codetrans.MethodSignature;
import io.vertx.codetrans.TypeArg;

import javax.lang.model.element.TypeElement;
import java.util.List;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ExpressionModel extends CodeModel {

  protected final CodeBuilder builder;

  public ExpressionModel(CodeBuilder builder) {
    this.builder = builder;
  }

  public ExpressionModel as(TypeInfo type) {
    switch (type.getKind()) {
      case API:
        return builder.api(this);
      case JSON_OBJECT:
        return new JsonObjectModel(builder, this);
      case JSON_ARRAY:
        return new JsonArrayModel(builder, this);
      case OTHER:
        if (type.isDataObjectHolder()) {
          return new DataObjectModel(builder, this);
        } else {
          return this;
        }
      case MAP:
        return new MapModel(builder, this);
      case LIST:
        return new ListModel(builder, this);
      default:
        return this;
    }
  }

  public ExpressionModel toDataObjectValue() {
    return this;
  }

  boolean isStringDecl() {
    return false;
  }

  void collectParts(List<Object> parts) {
    parts.add(this);
  }

  public ExpressionModel onMethodInvocation(TypeInfo receiverType, MethodSignature method, TypeInfo returnType,
                                            List<TypeArg> typeArguments, List<ExpressionModel> argumentModels,
                                            List<TypeInfo> argumentTypes) {
    if (method.getName().equals("equals") && method.getParameterTypes().size() == 1) {
      return builder.render(writer -> {
        writer.renderEquals(ExpressionModel.this, argumentModels.get(0));
      });
    } else {
      return new MethodInvocationModel(builder, ExpressionModel.this, receiverType, method,
          returnType, typeArguments, argumentModels, argumentTypes);
    }
  }

  public ExpressionModel onField(String identifier) {
    return builder.render((renderer) -> {
      renderer.renderMemberSelect(ExpressionModel.this, identifier);
    });
  }

  public ExpressionModel onMethodReference(MethodSignature signature) {
    return builder.render((renderer) -> {
      renderer.renderMethodReference(ExpressionModel.this, signature);
    });
  }

  public ExpressionModel onPostFixIncrement() {
    return builder.render((renderer) -> {
      renderer.renderPostfixIncrement(ExpressionModel.this);
    });
  }

  public ExpressionModel onPrefixIncrement() {
    return builder.render((renderer) -> {
      renderer.renderPrefixIncrement(ExpressionModel.this, renderer);
    });
  }

  public ExpressionModel onPostFixDecrement() {
    return builder.render((renderer) -> {
      renderer.renderPostfixDecrement(ExpressionModel.this);
    });
  }

  public ExpressionModel onPrefixDecrement() {
    return builder.render((renderer) -> {
      renderer.renderPrefixDecrement(ExpressionModel.this);
    });
  }

  public ExpressionModel onLogicalComplement() {
    return builder.render((renderer) -> {
      renderer.renderLogicalComplement(ExpressionModel.this);
    });
  }

  public ExpressionModel unaryMinus() {
    return builder.render((renderer) -> {
      renderer.renderUnaryMinus(ExpressionModel.this);
    });
  }

  public ExpressionModel unaryPlus() {
    return builder.render((renderer) -> {
      renderer.renderUnaryPlus(ExpressionModel.this);
    });
  }

  public ExpressionModel onInstanceOf(TypeElement type) {
    return builder.render((renderer) -> {
      renderer.renderInstanceOf(ExpressionModel.this, type);
    });
  }
}
