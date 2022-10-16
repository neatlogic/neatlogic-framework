/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.file.core.pattern.parser;

import codedriver.framework.file.core.pattern.*;
import codedriver.framework.file.core.util.DynamicClassLoadingException;
import codedriver.framework.file.core.util.IncompatibleClassException;

import java.util.Map;

class Compiler<E> {

    Converter<E> head;
    Converter<E> tail;
    final Node top;
    final Map converterMap;

    Compiler(final Node top, final Map converterMap) {
        this.top = top;
        this.converterMap = converterMap;
    }

    Converter<E> compile() {
        head = tail = null;
        for (Node n = top; n != null; n = n.next) {
            switch (n.type) {
                case Node.LITERAL:
                    addToList(new LiteralConverter<E>((String) n.getValue()));
                    break;
                case Node.COMPOSITE_KEYWORD:
                    CompositeNode cn = (CompositeNode) n;
                    CompositeConverter<E> compositeConverter = createCompositeConverter(cn);
                    if (compositeConverter == null) {
//                        addError("Failed to create converter for [%" + cn.getValue() + "] keyword");
                        addToList(new LiteralConverter<E>("%PARSER_ERROR[" + cn.getValue() + "]"));
                        break;
                    }
                    compositeConverter.setFormattingInfo(cn.getFormatInfo());
                    compositeConverter.setOptionList(cn.getOptions());
                    Compiler<E> childCompiler = new Compiler<E>(cn.getChildNode(), converterMap);
//                    childCompiler.setContext(context);
                    Converter<E> childConverter = childCompiler.compile();
                    compositeConverter.setChildConverter(childConverter);
                    addToList(compositeConverter);
                    break;
                case Node.SIMPLE_KEYWORD:
                    SimpleKeywordNode kn = (SimpleKeywordNode) n;
                    DynamicConverter<E> dynaConverter = createConverter(kn);
                    if (dynaConverter != null) {
                        dynaConverter.setFormattingInfo(kn.getFormatInfo());
                        dynaConverter.setOptionList(kn.getOptions());
                        addToList(dynaConverter);
                    } else {
                        // if the appropriate dynaconverter cannot be found, then replace
                        // it with a dummy LiteralConverter indicating an error.
                        Converter<E> errConveter = new LiteralConverter<E>("%PARSER_ERROR[" + kn.getValue() + "]");
//                        addStatus(new ErrorStatus("[" + kn.getValue() + "] is not a valid conversion word", this));
                        addToList(errConveter);
                    }

            }
        }
        return head;
    }

    private void addToList(Converter<E> c) {
        if (head == null) {
            head = tail = c;
        } else {
            tail.setNext(c);
            tail = c;
        }
    }

    /**
     * Attempt to create a converter using the information found in
     * 'converterMap'.
     *
     * @param kn
     * @return
     */
    @SuppressWarnings("unchecked")
    DynamicConverter<E> createConverter(SimpleKeywordNode kn) {
        String keyword = (String) kn.getValue();
        String converterClassStr = (String) converterMap.get(keyword);

        if (converterClassStr != null) {
            try {
//                return (DynamicConverter) OptionHelper.instantiateByClassName(converterClassStr, DynamicConverter.class, context);
                return (DynamicConverter) instantiateByClassName(converterClassStr, DynamicConverter.class);
            } catch (Exception e) {
//                addError("Failed to instantiate converter class [" + converterClassStr + "] for keyword [" + keyword + "]", e);
                return null;
            }
        } else {
//            addError("There is no conversion class registered for conversion word [" + keyword + "]");
            return null;
        }
    }

    /**
     * Attempt to create a converter using the information found in
     * 'compositeConverterMap'.
     *
     * @param cn
     * @return
     */
    @SuppressWarnings("unchecked")
    CompositeConverter<E> createCompositeConverter(CompositeNode cn) {
        String keyword = (String) cn.getValue();
        String converterClassStr = (String) converterMap.get(keyword);

        if (converterClassStr != null) {
            try {
//                return (CompositeConverter) OptionHelper.instantiateByClassName(converterClassStr, CompositeConverter.class, context);
                return (CompositeConverter) instantiateByClassName(converterClassStr, CompositeConverter.class);
            } catch (Exception e) {
//                addError("Failed to instantiate converter class [" + converterClassStr + "] as a composite converter for keyword [" + keyword + "]", e);
                return null;
            }
        } else {
//            addError("There is no conversion class registered for composite conversion word [" + keyword + "]");
            return null;
        }
    }

    // public void setStatusManager(StatusManager statusManager) {
    // this.statusManager = statusManager;
    // }
    //
    // void addStatus(Status status) {
    // if(statusManager != null) {
    // statusManager.add(status);
    // }
    // }

    private Object instantiateByClassName(String className, Class<?> superClass) throws IncompatibleClassException, DynamicClassLoadingException {
        if (className == null) {
            throw new NullPointerException();
        }
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            Class<?> classObj = classLoader.loadClass(className);
            if (!superClass.isAssignableFrom(classObj)) {
                throw new IncompatibleClassException(superClass, classObj);
            }
            return classObj.newInstance();
        } catch (IncompatibleClassException ice) {
            throw ice;
        } catch (Throwable t) {
            throw new DynamicClassLoadingException("Failed to instantiate type " + className, t);
        }
    }
}