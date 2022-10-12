/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubesys.kubernetes.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.ValueInstantiator.Gettable;
import com.github.kubesys.kubernetes.KubeStackClient;
import com.github.kubesys.kubernetes.KubeStackConstants;
import com.github.kubesys.kubernetes.annotations.ParameterDescriber;
import com.github.kubesys.kubernetes.api.specs.items.virtualmachine.Lifecycle.ResetVM;
import com.github.kubesys.kubernetes.api.specs.items.virtualmachine.Lifecycle.StopVMForce;
import com.github.kubesys.kubernetes.utils.RegExpUtils;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;

/**
 * @author  wuheng@iscas.ac.cn
 * 
 * @version 2.0.0
 * @since   2022.9.28
 * 
 * <p>
 * <code>AbstarctImpl<code> is used for selecting a optimal
 * machine based on the specified policy for each resource.
 * Here, resource can be VirtualMachine, VirtualMachinePool, 
 * VirtualMachineDisk, VirtualMachineSnapshot, and so on
 * 
 **/
public abstract class AbstractImpl<T, R> {

	/**
	 * m_logger
	 */
	protected final static Logger m_logger = Logger.getLogger(AbstractImpl.class.getName());

	/**
	 * client
	 */
	protected final KubeStackClient client;
	
	/**
	 * resource type
	 */
	protected final String kind;

	/**
	 * @param client         the client can manage the lifecyle of the specified 
	 * @param type           resource type, such as VirtualMachine, VirtualMachinePool
	 */
	public AbstractImpl(KubeStackClient client, String kind) {
		this.client = client;
		this.kind = kind;
	}

	private static JsonNode objectToJson(Object obj) throws Exception {
		return new ObjectMapper().readTree(
				new ObjectMapper().writeValueAsString(obj));
	}
	
	
	/*******************************************************
	 * 
	 *                Framework
	 * 
	 ********************************************************/
	/**
	 * @return                   Model, see fabric8 example
	 */
	protected abstract T getModel();
	
	/**
	 * @return                   Spec, see fabric8 example
	 */
	protected abstract R getSpec();
	
	/**
	 * @return                   Lifecycle, see fabric8 example
	 */
	protected abstract Object getLifecycle();
	
	
	/*******************************************************
	 * 
	 *                Extract 
	 * 
	 ********************************************************/
	
	protected ObjectMeta meta(T object) throws Exception {
		Method specMethod = object.getClass().getMethod("getMetadata");
		return (ObjectMeta) specMethod.invoke(object);
	}
	
	
	/*******************************************************
	 * 
	 *                Core
	 * 
	 ********************************************************/
	
	/**
	 * 
	 * @param name            resource name, the .metadata.name
	 * @return                the object, or null, or throw an exception
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public T get(String name) throws Exception  {
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(RegExpUtils.NAME_PATTERN);
		if (!pattern.matcher(name).matches()) {
			throw new IllegalArgumentException("the length must be between 4 and 100, and it can only includes a-z, 0-9 and -.");
		}
		return (T) client.getResource(this.kind, name);
	}
	
	
	/*******************************************************
	 * 
	 *                Common operators
	 * 
	 ********************************************************/
	/**
	 * @param name                  metadata.name
	 * @param nodeName              metadata.labels.host
	 * @param eventId               metadata.labels.eventId
	 * @return                      ObjectMeta  
	 */
	protected ObjectMeta createMetadata(String name, String nodeName, String eventId) {
		ObjectMeta om = new ObjectMeta();
		om.setName(name);
		Map<String, String> labels = new HashMap<String, String>();
		labels.put(KubeStackConstants.LABEL_HOST, nodeName);
		labels.put(KubeStackConstants.LABEL_EVENTID, eventId);
		om.setLabels(labels);
		return om;
	}
	
	
	/**
	 * Here, resource can be VirtualMachine, VirtualMachinePool, 
	 * VirtualMachineDisk, VirtualMachineSnapshot, and so on
	 * 
	 * @param object           resource object
	 * @return                 true or an exception
	 * @throws Exception       create resource fail
	 */
	@SuppressWarnings("unchecked")
	public boolean create(T object) throws Exception {
		client.createResource(objectToJson(object));
		m_logger.log(Level.INFO, "create "+ kind + " "
					+ meta(object).getName() + " successful.");
		return true;
	}

	/**
	 * 	Here, resource can be VirtualMachine, VirtualMachinePool, 
	 *  VirtualMachineDisk, VirtualMachineSnapshot, and so on
	 * 
	 * @param object            resource object
	 * @return                  true or an exception
	 * @throws Exception        delete resource fail
	 */
	public boolean delete(T object) throws Exception {
		client.deleteResource(objectToJson(object));
		m_logger.log(Level.INFO, "delete " + kind + " " 
					+ meta(object).getName() + " successful.");
		return true;
	}

	/**
	 * 
	 * Here, resource can be VirtualMachine, VirtualMachinePool, 
	 * VirtualMachineDisk, VirtualMachineSnapshot, and so on
	 *  
	 * @param object             resource object
	 * @return                   true or an exception
	 * @throws Exception         update resource fail
	 */
	public boolean update(T object) throws Exception {
		client.updateResource(objectToJson(object));
		m_logger.log(Level.INFO, kind + ": update " 
					+ meta(object).getName() + " successful.");
		return true;
	}
	
//	/**
//	 * 
//	 * Here, resource can be VirtualMachine, VirtualMachinePool, 
//	 * VirtualMachineDisk, VirtualMachineSnapshot, and so on
//	 *  
//	 * @param operator           lifecyle except for 'Create' and 'Delete'
//	 * @param object             resource object
//	 * @return                   true or an exception
//	 * @throws Exception         update resource fail
//	 */
//	protected boolean update(String operator, KubeStackModel<T> object) throws Exception {
//		client.updateResource(objectToJson(object));
//		m_logger.log(Level.INFO, kind + ": " + operator + " " 
//					+ object.getMetadata().getName() + " successful.");
//		return true;
//	}
	

//	
//	/**
//	 * @param name          resource name, the .metadata.name
//	 * @return              the object, or null, or throw an exception
//	 */
//	public String getEventId(String name) {
//		R resource = get(name);
//		return ((HasMetadata)resource).getMetadata().getLabels()
//				.get(ExtendedKubernetesConstants.LABEL_EVENTID);
//	}
	
	/**
	 * @return                  list all resource, or null, or throw an exception
	 */
//	@SuppressWarnings("unchecked")
//	public S list() {
//		return (S) client.list();
//	}
//	
	/**
	 * list all resources with the specified labels
	 * 
	 * @param  labels          resource labels, the .metadata.labels
	 * @return                 all resource, or null, or throw an exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public S list(Map<String, String> labels) {
		return (S) ((FilterWatchListDeletable) client.withLabels(labels)).list();
	}
	
	
	
	/**
	 * @param name                   name
	 * @param hostname               hostbame
	 * @return                       true or Exception
	 * @throws Exception             Exception
	 */
	public boolean updateHost(String name, String hostname) throws Exception {
		
		R res = get(name);
		HasMetadata metadata = (HasMetadata)res;
		Map<String, String> tags = metadata.getMetadata().getLabels();
		tags = (tags == null) ? new HashMap<String, String>() : tags;
		tags.put("host", hostname);
		metadata.getMetadata().setLabels(tags);
		
		Method specMethod = res.getClass().getMethod("getSpec");
		Object spec = specMethod.invoke(res);
		
		Method nodeMethod = spec.getClass().getMethod("setNodeName", String.class);
		nodeMethod.invoke(spec, hostname);
		boolean sucess = update(ExtendedKubernetesConstants.OPERATOR_UPDATE_HOST, metadata);
		
		if (!sucess) {
			throw new Exception("Target " + hostname + " is unreachable.");
		}
		
		while(true) {
			R res2 = get(name);
			HasMetadata metadata2 = (HasMetadata)res2;
			Map<String, String> tags2 = metadata2.getMetadata().getLabels();
			tags2 = (tags2 == null) ? new HashMap<String, String>() : tags2;
			if (hostname.equals(tags.get("host"))) {
				break;
			} else {
				Thread.sleep(1000);
			}
		}
		
		return sucess;
	}
	
	/**
	 * @param name                   name
	 * @param hostname               hostbame
	 * @param power                  power
	 * @return                       true or Exception
	 * @throws Exception             Exception
	 */
	public boolean updateHostAndPower(String name, String hostname, String power) throws Exception {
		
		R res = get(name);
		HasMetadata metadata = (HasMetadata)res;
		Map<String, String> tags = metadata.getMetadata().getLabels();
		tags = (tags == null) ? new HashMap<String, String>() : tags;
		tags.put("host", hostname);
		metadata.getMetadata().setLabels(tags);
		
		Method specMethod = res.getClass().getMethod("getSpec");
		Object spec = specMethod.invoke(res);
		
		Method nodeMethod = spec.getClass().getMethod("setNodeName", String.class);
		nodeMethod.invoke(spec, hostname);
		
		Method powerMethod = spec.getClass().getMethod("setPowerstate", String.class);
		powerMethod.invoke(spec, power);
		
		boolean sucess = update(ExtendedKubernetesConstants.OPERATOR_UPDATE_HOST, metadata);
		
		if (!sucess) {
			throw new Exception("Target " + hostname + " is unreachable.");
		}
		
		while(true) {
			R res2 = get(name);
			HasMetadata metadata2 = (HasMetadata)res2;
			Map<String, String> tags2 = metadata2.getMetadata().getLabels();
			tags2 = (tags2 == null) ? new HashMap<String, String>() : tags2;
			if (hostname.equals(tags.get("host"))) {
				break;
			} else {
				Thread.sleep(1000);
			}
		}
		
		return sucess;
	}
	
	/**
	 * @param name               resource name, the .metadata.name
	 * @param key                key
	 * @param value              value
	 * @return                   true, or false, or an exception
	 * @throws Exception         exception
	 */
	public boolean addTag(String name, String key, String value) throws Exception {

		R res = get(name);
		if (res == null) {
			m_logger.log(Level.SEVERE, type + " " + name 
				+ " not exist so that we cannot add this tag.");
			return false;
		}

		HasMetadata metadata = (HasMetadata)res;
		Map<String, String> tags = metadata.getMetadata().getLabels();
		tags = (tags == null) ? new HashMap<String, String>() : tags;
		tags.put(key, value);
		return update(ExtendedKubernetesConstants.OPERATOR_ADD_TAG, metadata);
	}
	
	/**
	 * @param name               resource name, the .metadata.name
	 * @param labels             map
	 * @return                   true, or false, or an exception
	 * @throws Exception         exception
	 */
	public boolean addTags(String name, Map<String, String> labels) throws Exception {

		R res = get(name);
		if (res == null) {
			m_logger.log(Level.SEVERE, type + " " + name 
				+ " not exist so that we cannot add this tag.");
			return false;
		}

		HasMetadata metadata = (HasMetadata)res;
		Map<String, String> tags = metadata.getMetadata().getLabels();
		tags = (tags == null) ? new HashMap<String, String>() : tags;
		tags.putAll(labels);
		return update(ExtendedKubernetesConstants.OPERATOR_ADD_TAG, metadata);
	}

	
	/**
	 * @param name               resource name, the .metadata.name
	 * @param keys               List
	 * @return                   true, or false, or an exception
	 * @throws Exception         exception
	 */
	public boolean deleteTags(String name, List<String> keys) throws Exception {

		R res = get(name);
		if (res == null) {
			m_logger.log(Level.SEVERE, type + " " + name + " not exist.");
			return false;
		}

		HasMetadata metadata = (HasMetadata)res;
		Map<String, String> tags = metadata.getMetadata().getLabels();
		if (tags != null) {	
			for (String key : keys) {
				tags.remove(key);
			}
		}
		return update(ExtendedKubernetesConstants.OPERATOR_DEL_TAG, metadata);
	}
	
	/**
	 * @param name               resource name, the .metadata.name
	 * @param key                key
	 * @return                   true, or false, or an exception
	 * @throws Exception         exception
	 */
	public boolean deleteTag(String name, String key) throws Exception {

		R res = get(name);
		if (res == null) {
			m_logger.log(Level.SEVERE, type + " " + name + " not exist.");
			return false;
		}

		HasMetadata metadata = (HasMetadata)res;
		Map<String, String> tags = metadata.getMetadata().getLabels();
		if (tags != null) {	tags.remove(key);}
		return update(ExtendedKubernetesConstants.OPERATOR_DEL_TAG, metadata);
	}
	
	/**
	 * @return                  api version
	 */
	public String getAPIVersion() {
		return "cloudplus.io/v1alpha3";
	}
	
	/**
	 * @return                  kind
	 */
	public String getKind() {
		return this.kind;
	}
	
//	/**
//	 * @return                     all support cmds
//	 * @throws Exception           an exception
//	 */
//	public List<String> getSupportCmds() throws Exception {
//		String rootPkg = ExtendedCustomResourceDefinitionSpec.class.getPackage().getName();
//		String fullPkg = rootPkg + "." + type.toLowerCase();
//		String className = fullPkg + ".Lifecycle";
//		Class<?> clazz = Class.forName(className);
//		
//		List<String> cmds = new ArrayList<String>();
//		for (Field f : clazz.getDeclaredFields()) {
//			cmds.add(f.getName());
//		}
//		
//		return cmds;
//	}
	
	/******************************************************
	 * 
	 *               Core
	 * 
	 *******************************************************/
	/**
	 * @param model              model
	 * @param om                 objectMeta
	 * @param spec               spec
	 * @return                   true, or an exception
	 * @throws Exception         exception  
	 */
	protected boolean create(T model, ObjectMeta om, R spec) throws Exception {
		
		// r.setApiVersion(apiversion)
		Method setVersion = model.getClass().getMethod("setApiVersion", String.class);
		setVersion.invoke(model, getAPIVersion());
		
		// r.setKind(kind)
		Method setKind = model.getClass().getMethod("setKind", String.class);
		setKind.invoke(model, getKind());
		
		// r.setMetadata(metadata)
		Method setMeta = model.getClass().getMethod("setMetadata", ObjectMeta.class);
		setMeta.invoke(model, om);
		
		// r.setSpec(spec)
		Method setSpec = model.getClass().getMethod("setSpec", spec.getClass());
		setSpec.invoke(model, spec);
		
		return create(model);
	}
	
	
	/**
	 * @param r                       r 
	 * @param operator                operator
	 * @return                        true or an exception
	 * @throws Exception
	 */
	public boolean update(R r, Object operator) throws Exception {
		
		T t = getSpec(r);
		Object lifecycle = createLifecycle(operator);
		
		// t.setLifecycle(lifecycle)
		Method setLifecycle = t.getClass().getMethod("setLifecycle", lifecycle.getClass());
		setLifecycle.invoke(t, lifecycle);
		
		return update(operator.getClass().getSimpleName(), (HasMetadata) r);
	}
	/**
	 * @param name                    name
	 * @param om                      ObjectMeta  
	 * @param operator                operator
	 * @return                        true or an exception
	 * @throws Exception
	 */
	public boolean update(String name, ObjectMeta om, Object operator) throws Exception {
		
		String oname = operator.getClass().getSimpleName();
		
		if (oname.startsWith("Batch")) {
			
			Method method = operator.getClass().getDeclaredMethod("get" + oname.substring("Batch".length()) + "s");
			List<?> values = (List<?>) method.invoke(operator);
			for (Object suboperator : values) {
				for (int i = 0; i < 3; i++) {
					try {
						doUpdate(name, om, suboperator);
						break;
					} catch (Exception ex) {
						if (i == 2) {
							throw new RuntimeException(ex);
						} else {
							Thread.sleep(3000);
						}
					}
				}
			}
			return true;
		} else {
			return doUpdate(name, om, operator);
		}
	}

	protected boolean doUpdate(String name, ObjectMeta om, Object operator)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, Exception {
		R r = get(name);
		if (r == null) {
			throw new RuntimeException(type + " " + name + " is not exist");
		}
		
		T t = getSpec(r);
		if (!operator.getClass().getSimpleName().equals(StopVMForce.class.getSimpleName())
				&& !operator.getClass().getSimpleName().equals(ResetVM.class.getSimpleName())) {
			Method glf = t.getClass().getMethod("getLifecycle");
			Object gva = glf.invoke(t);
			if (gva != null) {
				throw new RuntimeException(type + " " + name + " is now under processing");
			}
		}
		
		Object lifecycle = createLifecycle(operator);
		
		// t.setLifecycle(lifecycle)
		Method setLifecycle = t.getClass().getMethod("setLifecycle", lifecycle.getClass());
		setLifecycle.invoke(t, lifecycle);
		
		// r.setSpec(spec)
		Method setSpec = r.getClass().getMethod("setSpec", t.getClass());
		setSpec.invoke(r, t);

		// r.setMetadata(metadata)
		Method setMeta = r.getClass().getMethod("setMetadata", ObjectMeta.class);
		setMeta.invoke(r, om);
		
		return update(operator.getClass().getSimpleName(), (HasMetadata) r);
	}
	
	/**
	 * @param name                    name
	 * @param om                      ObjectMeta
	 * @param operator                operator
	 * @return                        true or an exception
	 * @throws Exception              exception
	 */
	public boolean delete(String name, ObjectMeta om, Object operator) throws Exception {
		
		R r = get(name);
		if (r == null) {
			throw new RuntimeException(type + " " + name + " is not exist");
		}
		
		T t = getSpec(r);
		Method glf = t.getClass().getMethod("getLifecycle");
		Object gva = glf.invoke(t);
		if (gva != null) {
			delete((HasMetadata) r);
			return true;
		}
		
		return update(name, om, operator);
	}
	
	/*************************************************
	 * 
	 *              Utils
	 * 
	 *************************************************/
	
	/**
	 * @param nodeName             nodeName
	 * @param lifecycle            lifecycle
	 * @return                     Spec, or an exception
	 * @throws Exception           exception
	 */
	public R createSpec(String nodeName, Object lifecycle) throws Exception {
		R t = getSpec();
		if (nodeName != null) {
			// t.setNodeName(nodeName)
			Method setNode = t.getClass().getMethod("setNodeName", String.class);
			setNode.invoke(t, nodeName);
		}

		// t.setLifecycle(lifecycle)
		if (lifecycle != null) {
			Method setLifecycle = t.getClass().getMethod("setLifecycle", lifecycle.getClass());
			setLifecycle.invoke(t, lifecycle);
		}
		
		return t;
	}
	
	/**
	 * @param operator               operator
	 * @return                       lifecycle, or an exception
	 * @throws Exception             exception
	 */
	public Object createLifecycle(Object operator) throws Exception {
		
		// JSR 303
		for (Field field : operator.getClass().getDeclaredFields()) {
			ParameterDescriber param = field.getAnnotation(ParameterDescriber.class);
			if (param == null) {
				continue;
			}
			
			
			String fieldName = field.getName();
			String method = "get" + fieldName.substring(0, 1)
						.toUpperCase() + fieldName.substring(1);
			Object value = operator.getClass()
					.getMethod(method).invoke(operator);
			
			if (param.required() == false && value == null) {
				continue;
			}
			
			if (value != null && !(value instanceof String)) {
				continue;
			}
			
			Pattern pattern = field.getAnnotation(Pattern.class);
			
			if (pattern == null || pattern.regexp() == null) {
				continue;
			}
			
			String regexp = pattern.regexp();
			
			java.util.regex.Pattern checker = java.util.regex.Pattern.compile(regexp);
			if (value == null || !checker.matcher((String)value).matches()) {
				throw new Exception(fieldName + "是必填项，约束：" + param.constraint() + ", constraint is: " + regexp);
			}
		}
		
		Object lifecycle = getLifecycle();
		String name = "set" + operator.getClass().getSimpleName();
		Method setOperator = lifecycle.getClass().getMethod(name, operator.getClass());
		setOperator.invoke(lifecycle, operator);
		return lifecycle;
	}
	

	/**
	 * @param name                      name
	 * @param eventId                   eventId
	 * @return                          ObjectMeta 
	 * @throws Exception 
	 */
	protected ObjectMeta updateMetadata(String name, String eventId) throws Exception {
		T r = get(name);
		if (r == null) {
			throw new RuntimeException(kind + " " + name + " is not exist");
		}

		Method m = r.getClass().getMethod("getMetadata");
		ObjectMeta om = (ObjectMeta) m.invoke(r);
		Map<String, String> labels = om.getLabels();
		labels = (labels == null) ? new HashMap<String, String>() : labels;
		labels.put(KubeStackConstants.LABEL_EVENTID, eventId);
		om.setLabels(labels);
		return om;
	}
	
}