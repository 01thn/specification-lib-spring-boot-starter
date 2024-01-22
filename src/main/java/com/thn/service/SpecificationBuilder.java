package com.thn.service;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

import com.thn.annotation.FilteredClass;
import com.thn.annotation.FilteredField;
import com.thn.annotation.OperationType;
import java.lang.reflect.Field;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class SpecificationBuilder {

  public Specification<Object> buildSpecification(Object filterDto) throws IllegalAccessException {
    Specification<Object> spec = Specification.where(null);
    Class<?> aClass = filterDto.getClass();

    if (aClass.isAnnotationPresent(FilteredClass.class)) {
      Field[] declaredFields = aClass.getDeclaredFields();
      for (Field field : declaredFields) {
        if (field.isAnnotationPresent(FilteredField.class)) {
          field.setAccessible(true);
          Object value = field.get(filterDto);
          if (value != null) {
            String name = field.getName();
            OperationType operationType = field.getAnnotation(FilteredField.class).operationType();

            spec = spec.and(getOperation(getFieldName(field), value, operationType));
          }
        }
      }
    }

    return spec;
  }

  private Specification<Object> getOperation(String name, Object value,
      OperationType operationType) {
    switch (operationType) {
      case EQUALS:
        return equals(name, value);
      case BIGGER_OR_EQUAL:
        return (value instanceof Number) ? ge(name, (Number) value) : null;
      case SMALLER_OR_EQUAL:
        return (value instanceof Number) ? le(name, (Number) value) : null;
      default:
        return null;
    }
  }

  private String getFieldName(Field field) {
    return isNotBlank(field.getAnnotation(FilteredField.class).fieldName())
        ? field.getAnnotation(FilteredField.class).fieldName() : field.getName();
  }

  private Specification<Object> equals(String name, Object value) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(name), value);
  }

  private Specification<Object> ge(String name, Number value) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.ge(root.get(name), value);
  }

  private Specification<Object> le(String name, Number value) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.le(root.get(name), value);
  }

}
