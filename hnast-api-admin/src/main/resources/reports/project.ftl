
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>
	项目申报书
</title>
<style type="text/css">
	@page{size:210mm 297mm;margin-top:60pt;margin-bottom:60pt;
		@top-center {content: element(header)}
		@bottom-center {content: element(footer)}
	}

	html, body{ width:100%; height:100%;}
	body {color: #000;font-family:"SimSun";font-size:16px;margin:0;padding:0;}
	a {color:#000;text-decoration:none;}
	a:hover {text-decoration:none;color:#229aff;}
	body, div, dl, dt, dd, ul, ol, li, h1, h2, h3, h4, h5, h6, pre, form, fieldset, input, textarea, p, blockquote, th, td {margin: 0;padding: 0;list-style:none;}
	img {display: block;}
	fieldset, img, html, body, iframe {border: 0 none;}
	.red{ color:#F00;}
	.wrap{ width:640px; height:950px; margin:10px auto; padding:0; overflow:hidden;}
	.wrap1{ width:640px; margin:auto; padding:0; overflow:hidden;margin-top: 10px;}
	.wrap1_border{  border: solid;border-width: 1px 1px 1px 1px;}
	.wrap h3,.wrap1 h3{ font-size:32px; margin:10px 0 0 0; line-height:60px; font-weight:bold; padding:0; overflow:hidden; text-align:center;}
	.name{ height:30px; line-height:30px; margin:15px 0; padding:0; overflow:hidden; text-align:center;}
	.fsize16{ text-align: left;font-size:16px; margin:0;}
	.cont_info{ width:640px; margin:100px 0 0 0; padding:0 40px 0 40px; overflow:hidden;}
	.cont_info p{ height:auto; min-height:50px; margin:0; padding:0px; overflow:hidden;}
	.cont_info p em{ float:left; width:100px; font-size:16px; margin:10px 30px 0 0; height:30px;font-weight:bold; font-style:normal;}
	.underline{ height:42px; float:left;}
	.textfiled{ height:auto; min-height:25px; padding:1px 0px; overflow:hidden; font-family:"SimSun"; font-size:16px; text-align:center; background:#fff; border:0; border-bottom:1px solid #000;}
	.textfiled i{ line-height:25px; font-style:normal;}
	.w520{ width:430px;}
	.w50{ width:50px; margin-left: 40px;}
	.w100{ width:100px;}
	.border_line{ text-decoration:underline;}
	.time{ text-align:center; margin:20px auto 0 auto; padding:0; overflow:hidden; font-size:15px;}
	.kxTitle{ text-align:center; margin:100px auto 0 auto; padding:0; overflow:hidden; font-size:20px;font-weight: bold;}
	.wrap h4,.wrap1 h4{ font-size:24px; margin:0 0 30px 0; line-height:60px; padding:0; overflow:hidden; text-align:center;}
	.wrap_con{ width:640px; margin:0; padding:0; overflow:hidden; line-height:30px; font-size:16px;}
	.wrap_con p{ margin:0; padding:0; overflow:hidden; text-indent:30px;}
	.bottom{ width:640px; margin:240px auto; padding:0; overflow:hidden;}
	.bottom p{ margin:15px 0; padding:0; overflow:hidden;}
	.bottom .txjfxx{ display:inline-block; height:auto; min-height:25px; padding:8px 0px; overflow:hidden; font-family:"SimSun"; font-size:16px; text-align:center; background:#fff; border-bottom:1px solid #000; }
	.bottom .txjfxx i{ line-height:25px; font-style:normal;}
	.aright{ text-align:right;}
	.wrap_info{ margin:0; padding:0; overflow:hidden;}
	.wrap_info p{ height:auto; min-hieght:30px; margin:0; padding:0; overflow:hidden;}
	.wrap_info p em{ float:left; width:130px; line-height:30px; margin:5px 5px 0 30px;font-size:16px; font-style:normal;}
	.wrap_info span{ height:auto; min-height:28px; line-height:28px; float:left;font-size:16px; }
	.wrap_info span i{ line-height:25px;}
	.ysbzimg{ position: relative; display: inline-block;top:3px;left: 5px;}
	.con_table{width: 640px;margin-top: 10px;table-layout:fixed; word-break:break-strict; }
	.con_table td{height: 40px;}
	.tdborder{border: solid;border-width: 0px 1px 1px 0px;}
	.tdborder2{border: solid;border-width: 0px 0px 1px 0px;}
	.tdborder3{border: solid;border-width: 1px 1px 1px 1px;}
	.tdborder4{border: solid;border-width: 0px 1px 1px 1px;}
	.tdborder5{border: solid;border-width: 0px 1px 1px 0px;}
	.td1{width:120px}
	.td2{width:200px}
	.tdh{font-weight:bold}
	.tdcon{height:auto;padding:20px 5px 20px 5px;line-height: 30px;}
	.td3{width:160px}
	.td3{width:320px}
	.h3{height:30px}
	.h5{height:60px}
	.emi{margin: 10px 0 0 0;}
	.titleFont{
		font-weight: bold;
		margin-left: 10px;
	}
	.tableAlign{
		text-align: center;
	}
</style>
<!--page1-->
<div class="wrap">
	<p class="name fsize16">
		项目编号：<#if project.code??>${project.code}</#if>
		</br>
	</p>
	</br>
	</br>
	</br>
	<h3 >
		长沙市科学技术协会项目申报书
	</h3>
	</br>
	</br>
	<div class="cont_info" >
		<p >
			<em class="w5 ">项目名称：</em><span class="underline textfiled w520" ><i>
			<#if project.name??>${project.name}</#if></i></span>
		</p>
		<p >
			<em class="w5 ">项目类型：</em><span class="underline textfiled w520" ><i>
				 <#list planTypes as planType>
					 <#if project.planType.id == planType.id>
						 ${planType.name}
					 </#if>
				 </#list>
			</i></span>
		</p>
		<#--		<#list planTypes as planType>-->
		<#--			<p >-->
		<#--				<em class="w4 "><#if planType_index==0>项目类型：</#if></em><span class="underline w520 emi" ><i>${planType_index+1}.${planType.name}	-->
		<#--			<#if project.planType??><#if project.planType.id??>-->
		<#--				<#if project.planType.id == planType.id>-->-->
		<#--					<img src="/pms/static/select.png" class="ysbzimg">-->
		<#--			<#else>-->
		<#--					<img src="/pms/static/noselect.png" class="ysbzimg">-->
		<#--				</#if></#if></#if>	-->
		<#--			</i></span>	-->
		<#--			</p>	-->
		<#--		</#list>	-->
		<br/>
		<br/>
		<br/>
		<p>
			<em class="w5 ">申报单位 ： （公章）</em><span class="emi underline textfiled w520 "><i><#if project.org.orgName??>${project.org.orgName}</#if></i></span>
		</p>
		<p>
			<em class="w5 style_yxqx">申报日期：</em><span class="underline textfiled w520 "><i>
			<#if project.applyDate??>${project.applyDate?string("yyyy-MM-dd")}</#if></i></span>
		</p>
	</div>
	<p class="kxTitle">
		长沙市科学技术协会
	</p>
	<p class="time">
		2021年制
	</p>
</div>
<!--page2-->
<div class="wrap">
	<h4>
		填 报 说 明
	</h4>
	<br/>
	<br/>
	<br/>
	<div class="wrap_con">
		<p style="margin:10px 0 0 0;">1．本申报书是申报长沙市科协项目的依据，从长沙市科协网站“长沙市科协项目管理系统”在线填写，内容须实事求是，表述应明确、严谨。相应栏目要求填写完整。
		</p>
		<p style="margin:10px 0 0 0;">2．每个申请项目单独填写项目申报书，同一申报书申请两个或两个以上项目视作无效。</p>
		<p style="margin:10px 0 0 0;">3．封面中“项目名称”须按长沙市科协年度项目指南中所设定的内容或申报通知要求填写，应确切反映项目内容和范围，最多不超过20个汉字。“项目类型”中，在所选项后面的“□”中划“√”。“申报单位” 须填写单位全称。“项目编号”由系统自动生成。</p>
		<p style="margin:10px 0 0 0;">
			4. 申报书中“项目负责人”应当填写项目单位直接组织实施该项目的责任人。“项目总体目标及预期绩效”是指项目预期达到的目标以及预期的社会、经济和生态效益等。“项目组织实施条件”，指项目单位在实施项目过程中应当具备的人员条件、资金条件、设施条件及其他相关条件。
		</p>
		<p style="margin:10px 0 0 0;">
			5.初审通过后的项目在“项目申报中心”的“项目管理”栏中显示“推荐” 状态，请项目申报单位在该栏中将“项目申报书”下载并按WORD格式A4纸打印一式五份，封面加盖单位公章后送长沙市科协“项目受理中心”（科普惠农项目和科普惠民项目需区、县（市）科协签署意见）。申请重点项目还需提交“项目可行性报告”一式五份。

		</p>
	</div>
</div>
<div class="wrap1 wrap1_border">
	<table class="con_table" cellspacing="0" cellpadding="0">
		<tr>
			<td class="tdh tdborder2" colspan="4"><span class="titleFont">一、项目申报单位基本情况</span></td>
		</tr>
		<tr>
			<td class="td1 tdborder">项目负责人</td>
			<td class="td2 tdborder"><#if project.org.chargeName??>${project.org.chargeName}</#if></td>
			<td class="td1 tdborder">职称/职务</td>
			<td class="td2 tdborder2"><#if project.org.chargeTitle??>${project.org.chargeTitle}</#if></td>
		</tr>
		<tr>
			<td class="td1 tdborder">联系电话</td>
			<td class="td2 tdborder"><#if project.org.orgPhone??>${project.org.orgPhone}</#if></td>
			<td class="td1 tdborder">手    机</td>
			<td class="td2 tdborder2"><#if project.org.chargeMobile??>${project.org.chargeMobile}</#if></td>
		</tr>
		<tr>
			<td class="td1 tdborder">电子邮箱</td>
			<td class="td2 tdborder"><#if project.org.chargeEmail??>${project.org.chargeEmail}</#if></td>
			<td class="td1 tdborder">传    真</td>
			<td class="td2 tdborder2"><#if project.org.corgFax??>${project.org.orgFax}</#if></td>
		</tr>
		<tr>
			<td class="td1 tdborder">单位地址</td>
			<td class="td2 tdborder"><#if project.org.orgAddress??>${project.org.orgAddress}</#if></td>
			<td class="td1 tdborder">邮政编码</td>
			<td class="td2 tdborder2"><#if project.org.orgPost??>${project.org.orgPost}</#if></td>
		</tr>
		<tr>
			<td class="tdh tdborder2" colspan="4"><span class="titleFont">二、项目申请理由及立项依据和目的</span></td>
		</tr>

		<tr>
			<td class="tdcon" colspan="4">
				<#if project.content.basis??>${project.content.basis}</#if>
			</td>
		</tr>
		</tbody></table>
</div>
<div class="wrap1 wrap1_border">
	<table class="con_table" cellspacing="0" cellpadding="0" >
		<tbody>
		<tr>
			<td class="tdh tdborder2" colspan="4"><span class="titleFont">三、项目主要内容</span></td>
		</tr>

		<tr>
			<td class="tdcon" colspan="4">
				<#if project.content.content??>${project.content.content}</#if>
			</td>
		</tr>
		</tbody></table>
</div>
<div class="wrap1 wrap1_border">
	<table class="con_table" cellspacing="0" cellpadding="0" >
		<tbody>
		<tr>
			<td class="tdh tdborder2" colspan="4"><span class="titleFont">四、项目总体目标及预期绩效</span></td>
		</tr>
		<tr>
			<td class="tdcon" colspan="4">
				<#if project.content.target??>${project.content.target}</#if>
			</td>
		</tr>

		</tbody></table>
</div>
<div class="wrap1">
	<table class="con_table" cellspacing="0" cellpadding="0" >
		<tbody>
		<tr>
			<td class="tdh tdborder3" colspan="4"><span class="titleFont">五、项目计划进度及阶段目标（项目实施步骤和进度计划）</span></td>
		</tr>

		<tr>
			<td class="tdborder3"colspan="4">
				项目起止时间： <#if project.startDate??>${project.startDate?string("yyyy-MM-dd")}</#if>  起到  <#if project.endDate??>${project.endDate?string("yyyy-MM-dd")}</#if>  止
			</td>
		</tr>

		<tr>
			<td class="td3 tdborder4 tableAlign">
				实施阶段
			</td>
			<td class="td3 tdborder tableAlign">
				经费预算（万元）
			</td>
			<td class="td3 tdborder tableAlign">
				目标内容
			</td>
			<td class="td3 tdborder5 tableAlign">
				时间跨度
			</td>
		</tr>
		<#list project.stages as stage>
			<tr>
				<td class="td3 tdborder4 ">
					<#if stage.name??>${stage.name}</#if>
				</td>
				<td class="td3 tdborder tableAlign">
					<#if stage.money??>${stage.money}</#if>
				</td>
				<td class="td3 tdborder">
					<#if stage.remarks??>${stage.remarks}</#if>
				</td>
				<td class="td3 tdborder5">
					<#if stage.startDate??>${stage.startDate?string("yyyy-MM-dd")}</#if>
					-
					<#if stage.endDate??>${stage.endDate?string("yyyy-MM-dd")}</#if>
				</td>
			</tr>
		</#list>
		</tbody></table>
</div>
<div class="wrap1 wrap1_border">
	<table class="con_table" cellspacing="0" cellpadding="0" >
		<tbody>

		<tr>
			<td class="tdh tdborder2" colspan="4"><span class="titleFont">六、项目组织实施条件</span></td>
		</tr>
		<tr>
			<td class="tdcon" colspan="4">
				<#if project.content.conditions??>${project.content.conditions}</#if>
			</td>
		</tr>
		</tbody></table>
</div>
<div class="wrap1 wrap1_border">
	<table class="con_table" cellspacing="0" cellpadding="0" >
		<tbody>
		<tr>
			<td class="tdh tdborder2" colspan="4"><span class="titleFont">七、项目经费预算</span></td>
		</tr>
		<tr>
			<td class="tdborder2" colspan="4">
				总预算：<span class="textfiled"><#if project.budget??>${project.budget}</#if></span>万元，其中申请市科协资助经费<span class="textfiled"><#if project.funds??>${project.funds}</#if></span>万元。
			</td>
		</tr>
		<tr>
			<td class="tdcon tdborder2" colspan="4">
				市科协资助经费支出预算明细
			</td>
		</tr>
		<tr>
			<td class="td3 tdborder tableAlign">
				支出内容
			</td>
			<td class="td3 tdborder tableAlign">
				支出金额（万元）
			</td>
			<td class="td4 tdborder2 tableAlign" colspan="2">
				资金用途说明
			</td>

		</tr>
		<#list project.fundsList as fund>
			<tr>
				<td class="td3 tdborder">
					<#if fund.name??>${fund.name}</#if>
				</td>
				<td class="td3 tdborder tableAlign">
					<#if fund.money??>${fund.money}</#if>
				</td>
				<td class="td4 tdborder" colspan="2">
					<#if fund.remark??>${fund.remark}</#if>
				</td>
			</tr>
		</#list>
		<tr>
			<td colspan="4">
				</br></br>
				开户银行：<#if project.bank??>${project.bank}</#if></br>
				</br></br>
				账    号：<#if project.cardNo??>${project.cardNo}</#if></br>
				</br></br>
				户    名：<#if project.accounts??>${project.accounts}</#if></br>
				</br></br>
			</td>
		</tr>
		<tr>
			<td colspan="4">
				申报单位意见：</br></br>
				</br></br></br></br>

				单位负责人签字</br>
				</br>
				</br>
				</br>
				</br>
				</br>
				<p style="text-align: right;">(单位公章)<span class="w50 "></span>年<span class="w50 "></span>月<span class="w50 "></span>日</p>

			</td>
		</tr>
		</tbody></table>
</div>
<div class="wrap1 wrap1_border">
	<table class="con_table" cellspacing="0" cellpadding="0" >
		<tbody>
		<tr>
			<td class="tdh tdborder2"><span class="titleFont">八、审核意见</span></td>
		</tr>

		<tr>
			<td class="tdcon tdborder2">
				区、县（市）科协意见：（适用于科普惠农项目和科普益民项目）
				</br></br></br>
			</td>

		</tr>
		<tr>
			<td class="tdcon tdborder2">
				归口部门初审意见：</br></br>


				部门负责人签字 </br> </br>
				<p style="text-align: right;">(部门公章)<span class="w50 "></span>年<span class="w50 "></span>月<span class="w50 "></span>日</p>

				分管领导签字 </br> </br>
				<p style="text-align: right;"><span class="w50 "></span>年<span class="w50 "></span>月<span class="w50 "></span>日</p>

			</td>
		</tr>
		<tr>
			<td class="tdcon tdborder2">
				市科协审核意见：</br></br>
				<p style="text-align: right;"><span class="w50 "></span>年<span class="w50 "></span>月<span class="w50 "></span>日</p>
			</td>
		</tr>
		<tr>
			<td class="tdcon tdborder2">
				专家评审意见：</br></br>
				评委会主任签字: </br>
				<p style="text-align: right;"><span class="w50 "></span>年<span class="w50 "></span>月<span class="w50 "></span>日</p>
			</td>
		</tr>
		<tr>
			<td  class="tdcon">
				审定结果：</br>
				<p style="text-align: left;text-indent:30px;">该项目通过专家评审，经公示无异议，报请财政审核，同意资助该项目人民币       万元。</p>
				</br>
				<p style="text-align: left;text-indent:60px;">市科协负责人签字 </p>
				<p style="text-align: left;text-indent:30px;"><span class="w50 "></span>年<span class="w50 "></span>月<span class="w50 "></span>日</p>
			</td>
		</tr>

	</table>
</div>
