package neatlogic.framework.annotationprocessor;

import neatlogic.framework.restful.core.IApiComponent;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@SupportedAnnotationTypes("neatlogic.framework.annotationprocessor.NeatLogicApi")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class NeatLogicApiAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }
        Elements elementUtils = processingEnv.getElementUtils();
        Types typeUtils = processingEnv.getTypeUtils();
        TypeElement componentTypeElement = processingEnv.getElementUtils().getTypeElement("org.springframework.stereotype.Component");
        TypeElement serviceTypeElement = processingEnv.getElementUtils().getTypeElement("org.springframework.stereotype.Service");
        TypeElement repositoryTypeElement = processingEnv.getElementUtils().getTypeElement("org.springframework.stereotype.Repository");
        TypeElement controllerTypeElement = processingEnv.getElementUtils().getTypeElement("org.springframework.stereotype.Controller");
        TypeElement authActionTypeElement = processingEnv.getElementUtils().getTypeElement("neatlogic.framework.auth.core.AuthAction");
        TypeElement authActionsTypeElement = processingEnv.getElementUtils().getTypeElement("neatlogic.framework.auth.core.AuthActions");
        TypeElement neatLogicApiTypeElement = elementUtils.getTypeElement("neatlogic.framework.annotationprocessor.NeatLogicApi");
        Set<? extends Element> neatLogicApiElements = roundEnv.getElementsAnnotatedWith(neatLogicApiTypeElement);
        Set<TypeElement> neatLogicApiClasses = ElementFilter.typesIn(neatLogicApiElements);
        TypeElement apiComponentTypeElement = elementUtils.getTypeElement(IApiComponent.class.getName());
        if (apiComponentTypeElement != null
                && authActionTypeElement != null
                && authActionsTypeElement != null
                && CollectionUtils.isNotEmpty(neatLogicApiClasses)
        ) {
            TypeMirror apiComponentTypeMirror = apiComponentTypeElement.asType();
            for (TypeElement neatLogicApiClass : neatLogicApiClasses) {
                if (implementsInterface(neatLogicApiClass, apiComponentTypeMirror, typeUtils)) {
                    boolean isBean = false;
                    List<? extends AnnotationMirror> annotationMirrors = neatLogicApiClass.getAnnotationMirrors();
                    for (AnnotationMirror annotationMirror : annotationMirrors) {
                        Element element = annotationMirror.getAnnotationType().asElement();
                        if (Objects.equals(element, componentTypeElement)
                                || Objects.equals(element, serviceTypeElement)
                                || Objects.equals(element, repositoryTypeElement)
                                || Objects.equals(element, controllerTypeElement))
                        {
                            isBean = true;
                            break;
                        }
                    }
                    if (isBean) {
                        boolean hasAuthAction = false;
                        for (AnnotationMirror annotationMirror : annotationMirrors) {
                            Element element = annotationMirror.getAnnotationType().asElement();
                            if (Objects.equals(element, authActionTypeElement) || Objects.equals(element, authActionsTypeElement)) {
                                hasAuthAction = true;
                                break;
                            }
                        }
                        if (!hasAuthAction) {
                            processingEnv.getMessager().printMessage(
                                    Diagnostic.Kind.ERROR,
                                    neatLogicApiClass.getQualifiedName() + "接口类需要加上@AuthAction注解进行权限控制, 如果未创建权限类, 可以先临时加上@AuthAction(action = NoAuth.class)使得编译通过",
                                    neatLogicApiClass
                            );
                        }
                    }
                }
            }
        }

        return true;
    }

    // 检查元素是否实现了指定的接口
    private boolean implementsInterface(TypeElement typeElement, TypeMirror interfaceType, Types typeUtils) {
        // 检查直接实现的接口
        for (TypeMirror implementedInterface : typeElement.getInterfaces()) {
            if (typeUtils.isSameType(implementedInterface, interfaceType)) {
                return true;
            }

            // 检查接口继承关系
            if (implementedInterface.getKind() == TypeKind.DECLARED) {
                TypeElement interfaceElement = (TypeElement) ((DeclaredType) implementedInterface).asElement();
                if (implementsInterface(interfaceElement, interfaceType, typeUtils)) {
                    return true;
                }
            }
        }

        // 检查父类的接口实现
        TypeMirror superClass = typeElement.getSuperclass();
        if (superClass.getKind() != TypeKind.NONE) {
            TypeElement superClassElement = (TypeElement) ((DeclaredType) superClass).asElement();
            return implementsInterface(superClassElement, interfaceType, typeUtils);
        }

        return false;
    }
}
