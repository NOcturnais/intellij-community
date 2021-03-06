// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.groovy.lang.psi.impl.statements.expressions.path;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.AtomicNotNullLazyValue;
import com.intellij.openapi.util.AtomicNullableLazyValue;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.openapi.util.NullableLazyValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.lang.psi.GroovyElementVisitor;
import org.jetbrains.plugins.groovy.lang.psi.api.GroovyReference;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrArgumentList;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.path.GrIndexProperty;
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.expressions.GrExpressionImpl;
import org.jetbrains.plugins.groovy.lang.psi.util.GroovyIndexPropertyUtil;
import org.jetbrains.plugins.groovy.lang.psi.util.GroovyLValueUtil;
import org.jetbrains.plugins.groovy.lang.resolve.references.GrIndexPropertyReference;

import static org.jetbrains.plugins.groovy.lang.psi.GroovyElementTypes.T_Q;
import static org.jetbrains.plugins.groovy.lang.resolve.ReferencesKt.referenceArray;

/**
 * @author ilyas
 */
public class GrIndexPropertyImpl extends GrExpressionImpl implements GrIndexProperty {

  private final NullableLazyValue<GrIndexPropertyReference> myRValueReference = AtomicNullableLazyValue.createValue(
    () -> GroovyLValueUtil.isRValue(this) ? new GrIndexPropertyReference(this, true) : null
  );

  private final NullableLazyValue<GrIndexPropertyReference> myLValueReference = AtomicNullableLazyValue.createValue(
    () -> GroovyLValueUtil.isLValue(this) ? new GrIndexPropertyReference(this, false) : null
  );

  private final NotNullLazyValue<GroovyReference[]> myReferences = AtomicNotNullLazyValue.createValue(
    () -> referenceArray(getRValueReference(), getLValueReference())
  );

  @Nullable
  @Override
  public GroovyReference getLValueReference() {
    return myLValueReference.getValue();
  }

  @Nullable
  @Override
  public GroovyReference getRValueReference() {
    return myRValueReference.getValue();
  }

  @NotNull
  @Override
  public GroovyReference[] getReferences() {
    return myReferences.getValue();
  }

  public GrIndexPropertyImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull GroovyElementVisitor visitor) {
    visitor.visitIndexProperty(this);
  }

  public String toString() {
    return "Property by index";
  }

  @Override
  @NotNull
  public GrExpression getInvokedExpression() {
    return findNotNullChildByClass(GrExpression.class);
  }

  @Nullable
  @Override
  public PsiElement getSafeAccessToken() {
    return findChildByType(T_Q);
  }

  @Override
  @NotNull
  public GrArgumentList getArgumentList() {
    return findNotNullChildByClass(GrArgumentList.class);
  }

  @Nullable
  @Override
  public PsiType getNominalType() {
    return GroovyIndexPropertyUtil.getSimpleArrayAccessType(this);
  }
}
