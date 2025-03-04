package com.glface.base.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.beans.BeanMap;
import org.apache.commons.beanutils.BeanUtils;

import java.util.*;

/**
 * 动态类
 */
public class DynamicBean {

	private Object object = null;// 动态生成的类对象
	private BeanMap beanMap = null;// 存放属性名称以及属性的类型

	private DynamicBean() {

	}

	/**
	 * 默认属性都是String类型
	 *
	 * @param propertyMap
	 *            key是属性名 value 为属性类型(Class)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DynamicBean(Map propertyMap) {
		this.object = generateBean(propertyMap);
		this.beanMap = BeanMap.create(this.object);
	}

	/**
	 * 给bean属性赋值
	 *
	 * @param property
	 *            属性名
	 * @param value
	 *            值
	 */
	public void setValue(Object property, Object value) {
		beanMap.put(property, value);
	}

	/**
	 * 通过属性名得到属性值
	 *
	 * @param property
	 *            属性名
	 * @return 值
	 */
	public Object getValue(String property) {
		return beanMap.get(property);
	}

	/**
	 * 得到该实体bean对象
	 *
	 * @return
	 */
	public Object getObject() {
		return this.object;
	}

	/**
	 */
	@SuppressWarnings("rawtypes")
	private Object generateBean(Map propertyMap) {
		BeanGenerator generator = new BeanGenerator();
		Set keySet = propertyMap.keySet();
		for (Iterator i = keySet.iterator(); i.hasNext();) {
			String key = (String) i.next();
			generator.addProperty(key, (Class) propertyMap.get(key));
		}
		return generator.create();
	}

	/**
	 * 构建一个动态对象
	 */
	public static class Builder {
		private Map typeMap = new HashMap<String, Class>();
		private Map typeValue = new HashMap<String, Object>();

		public Builder() {

		}

		public DynamicBean build() {
			DynamicBean bean = new DynamicBean(typeMap);
			for (Iterator iterator = typeValue.keySet().iterator(); iterator
					.hasNext();) {
				String pName = (String) iterator.next();
				Object value = typeValue.get(pName);
				bean.setValue(pName, value);
			}
			return bean;
		}

		/**
		 *
		 * @param propertyName
		 *            属性名称
		 * @param value
		 *            属性值
		 * @param propertyType
		 *            属性类型 默认为String类型
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public Builder setPV(String propertyName, Object value,
				Class propertyType) {
			if (propertyType == null) {
				propertyType = String.class;
			}
			typeMap.put(propertyName, propertyType);
			typeValue.put(propertyName, value);
			return this;
		}

		@SuppressWarnings("rawtypes")
		public Builder setPV(String propertyName, Object value) {
			Class type = String.class;
			if (value instanceof Integer) {
				type = Integer.class;
			} else if (value instanceof Long) {
				type = Long.class;
			} else if (value instanceof Float) {
				type = Float.class;
			} else if (value instanceof Double) {
				type = Double.class;
			} else if (value instanceof Date) {
				type = Date.class;
			} else if (value instanceof List) {
				type = List.class;
			} else if (value instanceof Boolean) {
				type = Boolean.class;
			}
			return setPV(propertyName, value, type);
		}
	}

	/**
	 * 通过现有的dynamicBean产生一个新的dynamicBean对象
	 */
	public DynamicBean createDynamicBean() {
		DynamicBean bean = new DynamicBean();
		try {
			bean.object = this.object.getClass().newInstance();
			bean.beanMap = BeanMap.create(bean.object);
			return bean;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 把source中的数据复制到动态对象object,source为javabean对象 注意：只有属性名相同的才会被复制
	 *
	 * @param source
	 * @return object null 拷贝失败
	 */
	public Object copy(Object source) {
		try {
			BeanUtils.copyProperties(object, source);
			return object;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 集合拷贝 把sourceList数据拷贝到动态类object中
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Object> copyList(List sourceList) {
		List objList = Lists.newArrayList();
		if (sourceList == null) {
			return objList;
		}
		Class objectClass = object.getClass();
		try {
			for (int i = 0; i < sourceList.size(); i++) {
				Object source = sourceList.get(i);
				Object ob = objectClass.newInstance();
				BeanUtils.copyProperties(ob, source);
				objList.add(ob);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objList;
	}

	public static void main(String[] args) {
		DynamicBean bean = new DynamicBean.Builder()
				.setPV("name", "ddd", String.class)
				.setPV("id", 11, Integer.class).build();

		Object object = bean.getObject();
		ObjectMapper objMap = new ObjectMapper();
		try {
			String json = objMap.writeValueAsString(object);
			System.out.println(json);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
