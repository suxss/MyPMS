/**
 * @author Pop
 * 
 * 此插件基于用于生成对密码的强度校验，是否相同等功能
 */
layui.define(['jquery','layer','util', 'form', 'laytpl', 'rate'], function(exports) {
	"use strict";
	var MODULE_NAME = 'password',
		ORIGN = "Pwd",
		CEHCK = "checkPwd",
		FOLLOW = "security",
		RATE = "rate",
		$ = layui.jquery,
		layer = layui.layer,
		util = layui.util,
		form = layui.form,
		laytpl = layui.laytpl,
		rate = layui.rate,
		groupId = 0,
		colors = ['#c2c2c2','#009688','#1E9FFF','#FF8000','#FE0000'],
		clazz={};
	
	var PWD_INPUT = [
		'<style type="text/css">',
		'.tooltip-{{ d.group }} {',
		    'position: absolute;',
		    'display: inline-block;',
		    'border-bottom: 1px dotted #c2c2c2;',
			'left:50%;',
			'{{# var style="";}}',
			'{{# if(d.position=="bottom"){style="top: 19.5%;"} }}',
			'{{=style}}',
		'}',
		'.tooltip-{{ d.group }} ul{',
			'padding: 10px 0px 10px 0;',
		'}',
		'.tooltip-{{ d.group }} .tooltiptext-{{ d.group }} {',
			'visibility: visible;',
			'width: 160px;',
		'	height: 35px;',
		'	line-height: 15px;',
		'	top: -50px;',
		'	border:1px solid #c2c2c2;',
		'	background-color: white;',
		'	color: #fff;',
		'	text-align: center;',
		'	border-radius: 6px;',
		'	padding: 5px 0;',
		'	position: absolute;',
		'	z-index: 1;',
		'	bottom: 150%;',
		'	left: 50%;',
		'	margin-left: -80px;',
		'}',
		'.tooltip-{{ d.group }} .tooltiptext-{{ d.group }}::after {',
		 '   content: "";',
		 '   position: absolute;',
		'    top: 100%;',
		'    left: 50%;',
		'    margin-left: -5px;',
		'    border-width: 5px;',
		 '   border-style: solid;',
		'    border-color: #c2c2c2 transparent transparent transparent;',
		'}',
		'.tooltip:hover .tooltiptext {',
		  '  visibility: visible;',
		'}',
		'</style>',
		'<div class="tooltip-{{ d.group }}">',
		  '<div class="tooltiptext-{{ d.group }}" id="{{ d.rate }}" style="display:none;"></div>',
		'</div>',
		'<div class="layui-form-item">',
		   '<label class="layui-form-label">{{ d.pwd }}</label>',
		   '<div class="layui-input-block">',
		     '<input type="password" name="{{ d.pwdName }}" id="{{ d.follow }}" lay-verify="{{ d.id }}" autocomplete="off" maxLength="{{ d.length }}" placeholder="请输入{{ d.pwd }}" class="layui-input">',
		   '</div>',
		'</div>',
		'<div class="layui-form-item">',
		   '<label class="layui-form-label">{{ d.checkPwd }}</label>',
		   '<div class="layui-input-block">',
		     '<input type="password" name="{{ d.checkPwdName }}" lay-verify="{{ d.cid }}" maxLength="{{ d.length }}" autocomplete="off" placeholder="请输入{{ d.checkPwd }}" class="layui-input">',
		   '</div>',
		'</div>'
	].join('');
	
	var Pwd = function(options){
		var that = this;
		that.v = "1.0.1";
		var config = $.extend({},that.config,options),
			i = groupId++;
		config.group =
		config.rate = RATE + i;
		config.id = ORIGN+i;
		config.cid = CEHCK+i;
		config.follow = FOLLOW+i;
		config.formObj = $("#"+config.next);
		that.render(config);
		config.rateObj = $("#"+config.rate);
		that.event(config);
		that.security(config);
	};
	Pwd.prototype.config = {
		event:'event',// 表单上的lay-filter
		length:16,//允许的密码长度
		minLength:6,//密码最少不能低于这个数值
		next:'next',//规定将会生成在哪个元素的后面
		pwd:'密码',
		pwdName:'password',
		position:'top',//会决定强度框显示在哪里
		checkPwd:'重复密码',
		checkPwdName:'checkPassword'
	},
	Pwd.prototype.render = function(options){
		laytpl(PWD_INPUT).render(options,function(html){
			options.formObj.after(html);
		});
		rate.render({
		    elem: '#'+options.rate
		    ,value: 0,
			length:5
			,theme:'#c2c2c2',
			readonly: true
		});
	},
	Pwd.prototype.security = function(options){
		var that =this,
			pwdInput = $("#"+options.follow);
		pwdInput.on('input propertychange', function(e){
			var result=that.level(options,e.target.value);
			rate.render({
			    elem: '#'+options.rate
			    ,value: result.value,
				length:5
				,theme:result.color,
				readonly: true
			});
		});
		pwdInput.focus(function() {
		  options.tipsId = layer.tips(['请输入',options.minLength,'-',options.length,'个由数字,大小写和特殊符号组成的密码,以确保账号安全。'].join(''), this,{time:0});
		  options.rateObj.show();
		  
		});
		pwdInput.focusout(function() {
		  options.rateObj.hide();
		  layer.close(options.tipsId);
		});
		
	},
	Pwd.prototype.level = function(options,value){
		// 0： 表示第一个级别 1：表示第二个级别 2：表示第三个级别
		  // 3： 表示第四个级别 4：表示第五个级别
		  var modes = 0,
			result = {value:0,color:colors[0]};
		  if(value.length > options.minLength){//最初级别
		   modes++
		  }
		  if(/\d/.test(value)){//如果用户输入的密码 包含了数字
		   modes++;
		  }
		  if(/[a-z]/.test(value)){//如果用户输入的密码 包含了小写的a到z
		   modes++;
		  }
		  if(/[A-Z]/.test(value)){//如果用户输入的密码 包含了大写的A到Z
		   modes++;
		  }
		  if(/\W/.test(value)){//如果是非数字 字母 下划线
		   modes++;
		  }
		  switch(modes){
		   case 1 :
		    result.value=1;
			result.color=colors[0]
		    break;
		   case 2 :
		    result.value=2;
		    result.color=colors[1]
		    break;
		   case 3 :
		    result.value=3;
		    result.color=colors[2]
		    break;
		   case 4 :
		    result.value=4;
		    result.color=colors[3]
		    break;
			case 5 :
			 result.value=5;
			 result.color=colors[4]
			break;
		  }
		  return result;
	},
	Pwd.prototype.event = function(options){
		var verifyObj={};
		verifyObj[options.cid]=function(value){
			if(value.length < 1){
			    return '请再输入一遍密码';
			}
			var pwd = form.val(options.event)[options.pwdName];
			if(pwd!==value){
				return options.pwd+'不一致';
			}
		}
		verifyObj[options.id]=function(value){
			if(value.length < 1){
			    return '请输入'+options.pwd;
			}
			if(value.length < options.minLength){
			    return options.pwd+'长度不得少于'+options.minLength;
			}
		}
		form.verify(verifyObj);
	},
	clazz.render = function(options){
		var pwd = new Pwd(options);
	};
	exports(MODULE_NAME, clazz);
});
