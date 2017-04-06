package com.lmq.main.util;

import android.util.DisplayMetrics;

public class Default {
	/**
	 * style = 0:TabBar样式 = 1：侧滑样式
	 */
	public static final int style = 0;
	/**
	 * is_photo = true上传身份证照片; = false：不上传身份证照片
	 */
	public static boolean is_photo = true;
	/**
	 * 是否显示输出文件，正式上线一定为false
	 */
	public static boolean showLog = true;

	public static final int  TYPE_YB_CLOSE_AUTOINVEST = 11;
	//------------------------------------------------------------支付方式
	/**使用银生宝支付*/
	public static final int PAY_YSB = 10001;
	/**使用融宝支付*/
	public static final int PAY_RB = 1002;
	/**使用宝付认证支付*/
	public static final int PAY_BF_RZ = 1003;
	/**使用宝付银联支付*/
	public static final int PAY_BF_YL = 1004;
	/**使用京东支付*/
	public static final int PAY_JD  = 1005;
	/**当前使用支付方式*/
	public static int PAY_TYPE = PAY_JD;






	/**
	 * 是否为钱多多托管
	 * 钱多多 true=测试,,false=正式
	 */
	public static final boolean IS_Qdd = true;
	public static final boolean IS_Qdd_TEST = true;

	/**
	 * 是否为支付通托管
	 * ZFT_Environment =true 测试环境; = false：正式测试环境
	 */
	public static final boolean IS_ZFT = false;
	public static boolean ZFT_Environment = true;

	/**
	 * 是否为易宝托管
	 *  true=测试,,false=正式
	 */
	public static final boolean IS_YB = false;
	public static final boolean IS_YB_TEST = true;
	public static final String YB_POST_URL = IS_YB_TEST?"http://220.181.25.233:8081/member/bhawireless/":"https://member.yeepay.com/member/bhawireless/";

	public static final String ip = IS_YB?"http://yeepay.lvmaque.net/":"http://xinyonghang.lvmaque.net/";



	public static final boolean USE_YBPAY = false;
	public static boolean has_Ybbind = false;
	public static boolean has_Ybbankcard = false;
	public static boolean has_ZFTbind = false;


	/**检测是否已绑定易宝*/
	public static final String Yb_isbind="/Mobile/Mcenter/is_binding";
	/**检测是否已绑定银行卡*/
	public static final String Yb_isbind_bankcard = "/Mobile/Mcenter/checkCard";
	//登录状态下注册易宝
	public static final int TYPE_YB_REGISTER = 0;
	/**登录状态下注册易宝和易宝托管信息*/
	public static final String Yb_register="/Mobile/Mobilecommon/ybbind";
	/**自动投标检测接口*/
	public static final String Yb_ckbid="/mobile/mcenter/ckbid";
	/**自动投标授权*/
	public static final String Yb_authorize="/mobile/mcenter/authorize";
	/**关闭自动投标授权*/
	public static final String Yb_closeInvest="/mobile/mcenter/closeInvest";
	//易宝充值
	public static final int TYPE_YB_CHARGE = 1;
	/**易宝充值*/
	public static final String Yb_charge = "/Mobile/Mcenter/ybrecharge";
	//易宝提现
	public static final int TYPE_YB_WITHDRAW = 2;
	/**易宝提现*/
	public static final String Yb_withdraw = "/Mobile/Mcenter/actwithdraw";
	//绑定银行卡
	public static final int TYPE_YB_BINDBANK = 3;
	/**绑定银行卡*/
	public static final String Yb_bindbankcard = "/Mobile/Mcenter/addbank";
	/**易宝自动授权功能*/
	public static final int  TYPE_YB_AUTOINVEST = 10;
	/**易宝自动授权功能*/


	public static boolean needRelogin = false;


	/**用户推出标志**/
	public static  boolean user_exit = true;

	public static final String RB_TEST_URL = "http://testapi.reapal.com/mobile/";
	public static final String RB_REAL_URL = "http://api.reapal.com/mobile/";
	public static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCw2d5Y4qy9/XlKXxpjhO2icprZQ7+JqinKH4kNOwuFUH2MHd05UpjeX0C/TeydDhqACUd7bOmCIRZFYPYV/EjnNglHdfrI9+I86jccogKgtkcSObm13yihRIJfptAQhwi14CcgSy+CZ7kaebgnT/0iy1EdiFQBhaf5SAlL5SgX6QIDAQAB";
	public static final String privateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALDZ3ljirL39eUpfGmOE7aJymtlDv4mqKcofiQ07C4VQfYwd3TlSmN5fQL9N7J0OGoAJR3ts6YIhFkVg9hX8SOc2CUd1+sj34jzqNxyiAqC2RxI5ubXfKKFEgl+m0BCHCLXgJyBLL4JnuRp5uCdP/SLLUR2IVAGFp/lICUvlKBfpAgMBAAECgYEAioFnSVfmEDo3Rw7qvOx7rFFzgxszJF8XsYw3KxyGLlfWq+krvFw7C1WwgONHn81O5d3elQS8cMT3C0kgEiHu+ZAK2DIWV9TzWiPhLCCe2lwXIDQVHTu9aPIuhusX8m9Gn746v22N1ZUOR1k6LqZ/9Z2R+7QJO6DSgOynAmuhxIECQQDiE/Yd7kyi1wbWZa9pY7H1Dg44Em7tw8wbxTyS4Ty1xsJWdfZoGc8t3nu+q7/S/z8YqFGWIt7cAK7hAilNCuLRAkEAyEH8g8M83j9fFF16rcfC7wqovy0+RSr5BbA6gbOJbkmumsWwfd1Ru74ULUMy7iIQa1fgQrUhMjGy1m6O6EY5mQJBALGpmiofUN5POLJXqqeZjwnTB92MrtvumIHiWB3dH0aCccpcXU2lqqiTRI+sjMyVhdIsxCDGOniNmOB+QFKpEWECQEPqLQs6oV/2OfacJUjeYHvVL5l2bJHkmwOU2qK2Eru2fWK7/LkS3+897XXUPmeIYe0lpka5SwLM3Avg/tJDTrECQQCktA0mvd7MCETj1a1bEQ0pXSwzFj7VnA2+azuUh6iYzLHtojrZnmTCygr5jeLnOg7uIYgbf5K5Nc2MlBDZGlGr";


	//支付通获取公钥串(测试)
	public static String TEST_ZFT_PUBLIC = "MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgQCFiMo8Vw/QPzTa1lhWwmtLS1S4WNkUgek9XV+d" +
			"KiflvI0CW+0gocQ7pLB2LonnE9vxnbPtnOEFnq0jI1Buz2cFti3WNovBuWDR2Vo1h59CLUeh4tHq" +
			"TAFgUecF5o+PDzyVFgu04dXefdEWPy3J87rsdmNy8yzTyZ/C1eR+QtTIVQICJxE=";
	//支付通获取公钥串(正式)
	public static String ZFT_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCOOzyHWK2EXdmUGoGBA1v41jitEv8M3vt9fZnwDMNzUtB//Kl9o1rX/jT8A" +
			"xmf501vlMN8yfJMlx5QZ25zeizZuGhfUQUQ0vs9BPPwXLyVfIQIOoDKGAPx1wqhpBNCRzdodXNGGJPwGeKsNBfTNCk/JivjGRZtJU" +
			"5julh6kSNYFQIDAQAB";

	// 获取手机分辨率
	public static DisplayMetrics dm;

	// 判断设置手势密码状态 false :关闭 true：打开
	public static boolean passwordSwitchChanged = false;



	/***************支付通接口--Start**********************/
	public static final String rechargePage = "/mobile/mcenter/rechargePage";
	public static final String icardPay = "/mobile/mcenter/icardPay";
	public static final String rechargeSure = "/mobile/mcenter/rechargeSure";
	public static final String zhifuPage = "/mobile/mcenter/zhifuPage";
	public static final String changeMobile = "/mobile/mcenter/changeMobile";

	/***************支付通接口--S **********************/



	// 企业直投和定投宝显示Flag FALSE 默认显示钱 TRUE 默认显示份数
	public static final boolean showUnitFlag = false;

	public static String user;
	public static boolean hdf_show_error_info = true;
	public static long userId = 0;
	public static String username = "";
	public static String user_photo_path = "";
	public static int phoneverif = 1; // 1.手动验证,2.验证码验证
	public static String curVersion = "1.0";
	public static boolean click_home_key_flag = false;
	public static boolean isActive = true;
	public static boolean isgestures = false;
	public static final boolean NEW_VERSION= true;
	//1:信用标,2:担保标,3:秒还标,4:净值标,5:抵押标,6: 企业直投,7:点金宝,
	// -----------------------------------------------------------
	public static final String login = "/mobile/Mobilecommon/actlogin";
	public static final String register = "/Mobile/Mobilecommon/regaction";
	public static final String peoInfoSafe = "/mobile/MCenter/accountinfo"; // 账户信息
	public static final String peoInfoUpdate = "/mobile/Mcenter/userinfo";
	public static final String peoInfoBorrowing = "/mobile/Mcenter/jiekuan";
	public static final String peoInfoInvestmrnt = "/mobile/Mcenter/touzi";
	// public static final String peoInfoBonus = "/mobile/Mcenter/jiangjin";
	public static final String peoInfoMoney = "/mobile/Mcenter/zjxx";
	public static final String peopleinfoPay = "/mobile/pay/unspay"; // 银生宝充值
	public static final String peopleinfoEmail = "/mobile/Mcenter/verifiEmail"; // 验证email
	public static final String verify_code = "/mobile/Mobilecommon/verify_code";
	public static final String peoInfoWithdrawal = "/mobile/Mcenter/tixian"; // 更新数据
	public static final String peoInfoWithdrawal_2 = "/mobile/Mcenter/validate"; // 我要提现—提交前确认
	public static final String peoInfoWithdrawal_3 = "/mobile/Mcenter/actwithdraw";// 提取现金
	public static final String peoInfosmrz = "/mobile/Mcenter/verify_personalid"; // 实名认证
	public static final String peoInfoPhone = "/Member/Mcenter/commitphone"; // 获取手机验证码
	public static final String registerPhone = "/mobile/mobilecommon/commitphone"; //注册获取手机验证码
	public static final String bannerPic = "/mobile/Main/bnlist"; // 获取banner图片
	public static final String bannerPicDetail = "/mobile/Main/bnedit"; // 获取banner图片详情

	public static final String peoInfoPhone2 = "/mobile/Mcenter/verifyphone"; // 验证手机号
	public static final String peoInfobindbankcard = "/mobile/MCenter/bind_debitcard"; // 绑定银行卡
	public static final String peoInfoxsbankcard = "/mobile/MCenter/obtain_bound_debit"; // 显示绑定银行信息
	public static final String peoInfoxsjiaoyipsw = "/mobile/MCenter/change_pay_passwd"; // 交易密码
	public static final String zq_request = "/mobile/Main/ajax_debt"; // 债权转让
	public static final String zq_buy_request = "/mobile/Main/buy_debt"; // 债权转让
	public static final String get_bf_bank_list = "/mobile/Main/buy_debt"; // 宝付支付链接



	/**融宝支付接口*/

	public static final String pay_rongbao_type = "/Mobile/Rbpay/Pay";

	/**宝付认证支付接口*/
	public static final String peopleinfoPayBaofu = "/mobile/BfPay/pay";
	/**宝付银联支付接口*/
	public static final String peopleinfoPayBaofu2 = "/mobile/BfPay/pay2";
	/**京东支付接口*/
	public static final String peopleinfoPayJD = "/mobile/JdPay/jdpayapp";


	/**添加银行卡页 双钱专属*/
	public static final String addbank_mmm = "/Mobile/Mcenter/addbank_mmm";

	public static final String tzList = "/mobile/Main/index_class";
	public static final String tzList2 = "/mobile/Main/index";
	public static final String tzListItem = "/mobile/Main/detail";
	public static final String tzListItem2 = "/mobile/Main/ajax_invest";
	public static final String tzListItem3 = "/mobile/Main/investcheck";
	public static final String tzListItem4 = "/mobile/Main/investmoney";
	public static final String tztListItem = "/mobile/Main/tdetail";
	public static final String tztListItem2 = "/mobile/Main/tajax_invest";
	public static final String tztListItem3 = "/mobile/Main/tinvestcheck";
	public static final String tztListItem4 = "/mobile/Main/tinvestmoney";
	// 新闻
	public static final String news = "/mobile/Main/getArticle";
	public static final String newsListItem = "/mobile/Main/gg_show";
	public static final String exit = "/mobile/Mobilecommon/mactlogout";
	public static final String changepass = "/mobile/Mcenter/changepwd"; // 登录密码
	public static final String tradinglog = "/mobile/Mcenter/tradinglog"; // 交易记录
	public static final String showPtbjl = "/mobile/Main/investlog";
	public static final String showLzbjl = "/mobile/Main/tinvestlog";
	public static final String chargeMoney = "/mobile/mcenter/chargeMoney";//双钱充值
	public static final String withdrawMoney = "/mobile/mcenter/withdrawMoney";//双钱提现
	// 检测更新
	public static final String version = "/mobile/Main/version";
	public static final String getBankInfo = "/mobile/MCenter/bankinfo";
	public static final String getCity = "/mobile/MCenter/getcity";

	public static final String Expand_list = "/mobile/MCenter/get_coupon";

	// 活动
	public static final String notice = "/mobile/Main/getNews";
	public static final String noticeListItem = "/mobile/Main/event_show";

	// public static List<String> LEND_MONEY_STATUE = new ArrayList<String>();

	// 意见反馈
	public static final String FEEDBACK = "/mobile/Main/feedback";
	public static final String FORGOT_PWD_1 = "/mobile/Main/getpassword";// 获取手机验证码
	// public static final String FORGOT_PWD_2 = "/mobile/Main/validatephone";
	public static final String FORGOT_PWD_3 = "/mobile/Main/repreatphone";// 修改密码
	public static final String GET_REGISTER_CONTEXT = "/Mobile/Api/ruleserver";// 注册协议

	// 系统信息
	public static String OS_VERSION = "";
	public static String PHONE_MODEL = "";

	// 借款列表
//	public static final String LENDMONEY = "/mobile/MCenter/credit_list";
	// 借款管理-列表
	public static final String SUMMALIST = "/mobile/Mcenter/summaList";
	// 借款管理-详情页
	public static final String PAYLIST = "/mobile/mcenter/paylist";
	// 借款管理-还款功能
	public static final String DOPAY = "/mobile/mcenter/dopay";

	// 借款管理-双钱还款功能
	public static final String SPDPPAY = "/mobile/main/dopay";

	// 借款管理-撤销借款
	public static final String DOERASE = "/mobile/mcenter/doerase";

	// 借款申请
	public static final String LENDMONEY_REQUEST = "/mobile/MCenter/request_credit";

	//修改手机号
	public static final String Yb_Changephone = "/Mobile/Mcenter/resetPhone";

	/*** 钱多多 绑定托管 */
	public static final String MoneyMm = "mobile/mcenter/bindingAccount";
	/*** 钱多多 开启还款授权 */
	public static final String MoneyMmsq2 = "/mobile/mcenter/borrowAuthorize";
	/*** 钱多多 开启投标授权 */
	public static final String MoneyMmsq1 = "/mobile/mcenter/investAuthorize";
	// 用户地理位置信息
	public static double L_Lat;
	public static double L_log;

	/**灵活宝投资页*/
	public static final String flexible_index = "/Mobile/Mcenter/flexible_index";
	/**购买灵活宝投资弹出页*/
	public static final String flexible_ajax_index = "/Mobile/Mcenter/flexible_ajax_index";
	/**购买灵活宝最终提交*/
	public static final String flexible_save = "/Mobile/Mcenter/flexible_save";

	/**快速计算投标收益*/
	public static final String quickcountrate = "/Mobile/Main/quickcountrate";

	/**我要提现*/
	public static final String validate_index = "/Mobile/Mcenter/validate_index";
	/**确认提现*/
	public static final String actwithdraw = "/Mobile/Mcenter/actwithdraw";

	/**灵活宝资金记录*/
	public static final String getLog = "/Mobile/Mcenter/getLog";

	/**债券标投标页*/
	public static final String debt_ajax_invest = "/Mobile/Main/debt_ajax_invest";
	/**债券标立即投标*/
	public static final String debt_investmoney = "/Mobile/Main/debt_investmoney";

	public static final String 双钱债权转让 = "mobile/main/buy_debt";
	public static final String debt_detail = "/Mobile/Main/debt_detail"; // 债权转让详情页

	/**银行卡列表页*/
	public static final String bank_index = "/Mobile/Mcenter/bank_index";
	/**添加银行卡页*/
	public static final String addbank = "/Mobile/Mcenter/addbank";
	/**修改银行卡页*/
	public static final String edit = "/Mobile/Mcenter/edit";
	/**修改银行卡提交*/
	public static final String doedit = "/Mobile/Mcenter/doedit";
	/**银行卡验证码获取*/
	public static final String getcode = "/Mobile/Mcenter/getcode";

	public static final String revisephone = "/Mobile/Mcenter/verifyphone"; // 修改手机号提交

	/********奖励管理********/

	/**奖励管理之红包生成*/
	public static final String send = "/Mobile/Mcenter/send";
	/**优惠券列表*/
	public static final String get_coupon = "/Mobile/Mcenter/get_coupon";
	/**奖励管理之优惠券奖励列表*/
	public static final String expLog = "/Mobile/Mcenter/expLog";
	/**奖励管理之红包列表*/
	public static final String bonus_index = "/Mobile/Mcenter/bonus_index";
	/**奖励管理之积分记录列表和积分兑换列表*/
	public static final String integral_index = "/Mobile/Mcenter/integral_index";
	/**奖励管理之积分兑换/*/
	public static final String ajaxcredit = "/Mobile/Mcenter/ajaxcredit";
	/**奖励管理之我要邀请/*/
	public static final String invite_link = "/Mobile/Mcenter/invite_link";
	/**奖励管理之邀请列表*/
	public static final String invite_index ="/Mobile/Mcenter/invite_index";

	/********奖励管理********/

	//站内信之列表页
	public static final String msg_index ="/Mobile/Mcenter/msg_index";
	//站内信之标记已读
	public static final String changestatus ="/Mobile/Mcenter/changestatus";

	/**我要借款之信用额度申请*/
	public static final String credit_apply ="/Mobile/Mcenter/credit_apply";
	/**我要借款之借款类型详细信息/*/
	public static final String borrow_index ="/Mobile/Mcenter/borrow_index";
	/**提交借款/*/
	public static final String request_credit ="/Mobile/Mcenter/request_credit";


	/********灵活宝********/
	public static final String getEndItem = "/mobile/mcenter/getEndItem";//回款
	public static final String user_index = "/mobile/mcenter/user_index";//结清
	public static final String iteminfo = "/mobile/mcenter/iteminfo"; //灵活宝详情
	public static final String redeemSave = "/mobile/mcenter/redeemSave"; //灵活宝详情
	public static final String getLog2 = "/mobile/mcenter/getLog2"; //灵活宝详情
	public static final String getRecord = "/Mobile/Mcenter/getRecord"; //灵活宝详情
	/********投资管理********/
	public static final String invest_index = "/Mobile/Mcenter/invest_index";//投资管理总表
	public static final String canTransfer = "/Mobile/Mcenter/canTransfer"; //可装让债权
	public static final String onBonds = "/Mobile/Mcenter/onBonds"; //可装让债权
	public static final String successDeb = "/Mobile/Mcenter/successDebt"; //已转让让债权
	public static final String buydetb = "/Mobile/Mcenter/buydetb"; //已经购买债权
	public static final String sellhtml = "/Mobile/Mcenter/sellhtml"; //可转让转让债券详情页
	public static final String sell_debt = "/Mobile/Mcenter/sell_debt"; //可转让转让债券详情页
	public static final String cancel_debt = "/Mobile/Mcenter/cancel_debt"; //可转让转让债券详情页
	public static final String debt_download = "/Mobile/Mcenter/debt_download"; //可转让转让债券详情页

	/********投资管理********/

	/********资料********/
	public static final String business = "/Mobile/Mcenter/business"; //
	public static final String yindaoye = "/Mobile/Mcenter/yindaoye"; //
	public static final String editdata = "/Mobile/Mcenter/editdata"; //上传资料
	public static final String delfile = "/Mobile/Mcenter/delfile"; //删除资料
	public static final String editfinancial = "/Mobile/Mcenter/editfinancial"; //
	public static final String people = "/Mobile/Mcenter/people"; //
	public static final String editcontact    = "/Mobile/Mcenter/editcontact"; //
	public static final String editdepartment    = "/Mobile/Mcenter/editdepartment"; //
	public static final String tenddetail    = "/Mobile/Mcenter/tenddetail"; //
	public static final String autolong    = "/Mobile/Mcenter/autolong"; //
	public static final String savelong    = "/Mobile/Mcenter/savelong"; //

	/********资料********/



	// -----------------------------------------------------------

	public static final int ANIMATION_LEFT_TO_RIGHT = 1;
	public static final int ANIMATION_RIGHT_TO_LEFT = 2;
	public static final int ANIMATION_TO_LEFT = 3;
	public static final int ANIMATION_TO_RIGHT = 4;

	// 本地存储数据-----------------------------------------------------------
	public static final String userPreferences = "lmq";
	public static final String userName = "name";
	public static final String userLastUid = "lastuid";
	public static final String userLastSl = "lastsl";
	public static final String userPassword = "password";
	public static final String userRemember = "remember";
	public static final String userPageStyle = "pagestyle";
	// -----------------------------------------------------------

	public static final int pageStyleLogin = 0;
	public static final int pageStyleInfo = 1;
	public static int layout_type;

	// -----------------------------------------------------------
	public static final int MESSAGE_TOAST = 0;
	public static final int MESSAGE_DIALOG = 1;
	public static final int MESSAGE_BITMAP = 2;
	// -----------------------------------------------------------

	// -----------------------------------------------------------
	public static final int RESULT_BANKCARD = 100;
	public static final int RESULT_REGISTER_TO_LOGIN = 100;
	public static final int RESULT_LOGIN_TO_PEOPLE = 101;
	// -----------------------------------------------------------

	// -----------------------------------------------------------
	public static final int LOGIN_TYPE_2 = 101;
	public static final int LOGIN_TYPE_3 = 102;
	public static final int LOGIN_TYPE_4 = 103;
	// -----------------------------------------------------------

	public static final String PIC_PATH = "/lmqdata/";

	public static boolean isAlive = false;
	public static boolean showNewsList = false;
	public static boolean showNoticeList = false;
	public static long showNewsId;
	public static long showNoticeId;
	// 标记显示新闻还是公告 false 显示新闻 true显示notice
	public static boolean IS_SHOW_NESW_OR_NOTICE = false;

	// 微信支付APPID 和 商家 ID
	public static final String APP_ID = "wxd930ea5d5a258f4f";

	/** 商家向财付通申请的商家id */
	public static final String PARTNER_ID = "1900000109";

	/**绿麻雀通讯加密私钥*/
	public  static final String LMQ_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDKqUFsIpI+ulvM"
			+ "ZzpEhRMhimiR1N7Owp7q2PsOnwV6yoqJjhFh67JA/mF0CA7spB6I2R1wYLhifRS+"
			+ "iG/L2PwKxu6ZJUDPYDYrBfVJYO6rYu5SsLuvNN5USWzFJD3wx8VP1IfBe7kXbnIe"
			+ "aJEFrrGCoqFGxb8IEf2XG2AHOjmFF6jcnhJ/mf2azhC269TxSAtc2q9echlaFYNJ"
			+ "2sXInObccKNxm4ymMn77HyKHGHuEBmWwMmckmorkQ9qh5A7ucQsXQfVH/9y22pJ/"
			+ "ABZ2joG5oKJ4mzB7fVqu55i2dp9ZCTT74HoE4+4S36tzWQjbeFXByEz1Ghr11aYw"
			+ "rsWxXxcfAgMBAAECggEABP2yHMWlMGJKjHgmWTWlyPetp5nEevcUDtiVJkEIU4PV"
			+ "cZnMF8tEMpGRONhR9cX88oXWUcQwBkzqS970UeLgIivQCHmAZRsLenfFGK+IcTQY"
			+ "hnTPynrU1uKQCTLhS6K/2z1ysnMnlcokiVct6yPoPz1eF24bTEmVIzmLXeIzNHHb"
			+ "fwYSHs+DlPmVjss80Vn/cn5+N0u3oFAWNopFNzIkRbz3ZulZDZqRxmIbHYCrhQOo"
			+ "gygbr4oHFY7X4SCQ7lBz7QpNIlQQ0H+Q2lGho0SDFi8brMtulPQSQtDW/ikurboj"
			+ "0dv2itkv5Hp63nN8INOHg9CJSOFPYk1KTJa2WZoFsQKBgQDrnU4l+jgUaksnSX7a"
			+ "W5pAEfZIfy20rs94cPUESe184UU/USng5hnpxN/eTBXapr/qmmpvWauHAlyo0m/F"
			+ "E4uXJu6DvthwJGdnhs3rRT1FJp8IBuqd02wVn+FggvzSwFaCGeDZjbh6Gt9QSkGW"
			+ "s/Ey4AmbVDGi1twlxH1hYRvktQKBgQDcMg62IsihGh7m1GW+iS2CQMAgKiSGSj+d"
			+ "TjGcdttGeJgEqV2w/gO+nf+VgIQ2AoVdxWzKfE630Hee8LEvs/m3loPzf3JszMJC"
			+ "dl/bHAofR3naifDARpN00aAail4RSja8ZlDFxO6+4wFpSbWmITzaBjey/XF33YT8"
			+ "KMfsq5BlAwKBgHiNV/red+saJ7HGTP10eQvnJNgbGkukVcsOII89NCH9MO0gngjQ"
			+ "MtoFPXYD//qL7w4eS+8FNloxvy8x3LY9NeL0rH0EHMHkHnQ96QZDuwGyrRPyVgDC"
			+ "uD6oOyyiWLGMb7bJft5RQN5Y+YGfPMfa8cJSFxg+j9b9kE5SpsQ4Z4kxAoGAGKa+"
			+ "AOQKBAcxmHQE4/zljGSFXdR5/FLJpmm/oh2oVM5hbnwrkgoOD0QO/gTCSG2l0qas"
			+ "lEqwYWHEUpXtsEJ83XHLWfJVtBqEUP6Bor0T4QaWfMHeoxgDoBtryizddSAS2qq1"
			+ "tj5i4Bn36XqoDG5CKsJxb+dx0ZKKNdS29ScL5lMCgYEAw2WY1nUBU9MYgBCvOQPE"
			+ "7LWiQpMISyGKpW6Fe5qHjo8Il9bOxgIev/BVjyN1JbH+R+DOZpY0O7LAlKxQMgLx"
			+ "fBMtxZSqSCl6z7p6PB5Ki53FcJfE0JyF7tPYGt7IlSbNfKSsyA6VSHTs3oafR+gd"
			+ "Z+MOguXlKPhuHopUNKwkOO0=";

	/**绿麻雀通讯加公钥*/
	public static final String LMQ_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnj+6sKcc3QVuteyPXi+l"
			+ "CqZA8jmHOWwObXfiBWHArWEMEMjxWG+ZzBDAHQmKJgRUzycqDiebwfVjUWSROobP"
			+ "FVYBRZRC1BAVdwdsJIGbh54v+3oTQJ2awegKihdGlPprpy8QFqdBEnvDQpmmSy/5"
			+ "J3OJKGXflf00gd5dDLaEtgUtwilsUJY3w2uLlEwnX/xgW3iSHMZe04ZDyKoXhRYU"
			+ "f1bt0q5uY0xKlbEHXp5ObEgpAwgMrCfCh2m6nX6FQAr/YSRYU45+GRoaodLdRB5t"
			+ "wqGfmdrnt1WsuocNJ0q0V3YHJnOYL4+jjwFQrimeEL5X3cGCyji/C7EucM8fjJSF"
			+ "uwIDAQAB";
}