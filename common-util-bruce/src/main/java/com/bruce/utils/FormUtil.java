package com.bruce.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 *  To help you get the value of the parameters passed from the page
 */
public  class FormUtil {

	private volatile static FormUtil instance=null;

	private FormUtil(){  }

	/**  Returns singleton class instance */
	public static FormUtil getInstance(){
		if(instance==null){
			synchronized (FormUtil.class) {
				if(instance==null){
					instance=new FormUtil();
				}
			}
		}
		return instance;
	}


	/**
	 * 根据实体类的属性名和属性类型,<br>
	 * 将页面传递到服务器的参数封装到 实体 Bean 中<br>
	 * <b style="color:red;">要求属性名  必须和 页面参数名 相同, 实体Bean 必须要有get,set方法</b><br>
	 * 支持的属性类型:  String , int , boolean , double , Integer , float
	 * @param clazz    实体Bean.class
	 * @param request  HttpServletRequest对象
	 * @return  T   实体对象,
	 */
	public <T>T getFormValues(Class<T> clazz,HttpServletRequest request){

		T tInstance=null;
		try {
			tInstance = clazz.newInstance();
			Field fields[] = clazz.getDeclaredFields(); //获取该类申明的成员变量,可能public,可能private
			for (Field field:fields) {
				try {
					String strValue=request.getParameter(field.getName());
					if(strValue!=null){
						//构建 setXxx(value); 方法
						String methodName = "set"+field.getName().substring(0,1).toUpperCase() + field.getName().substring(1);

						Class<?> fieldType=field.getType();
						Method method = clazz.getMethod(methodName,fieldType);
						//获取属性类型名
						String typeName=fieldType.getName();

						if(typeName.equals("java.lang.String")){
							method.invoke(tInstance,strValue);

						}else if(typeName.equals("int")){
							method.invoke(tInstance, Integer.parseInt(strValue));

						}else if(typeName.equals("boolean")){
							method.invoke(tInstance, Boolean.parseBoolean(strValue));

						}else if(typeName.equals("double")){
							method.invoke(tInstance, Double.parseDouble(strValue));

						}else if(typeName.equals("java.lang.Integer")){
							method.invoke(tInstance, Integer.parseInt(strValue));

						}else if(typeName.equals("float")){
							method.invoke(tInstance, Float.parseFloat(strValue));

						}else if(typeName.equals(String[].class.getName())){
							//String[],需要调用request.getParameterValues()方法
							String[] parameterValues = request.getParameterValues(field.getName());
							method.invoke(tInstance, new Object[]{parameterValues});
						}
					}
				} catch (Exception e) {  //e.printStackTrace();
					Logger.getLogger("lwl").info("异常信息 Exception "+e.getMessage());
				}
			}
		}catch (Exception e1) {
			e1.printStackTrace();
		}
		return tInstance;
	}


}
