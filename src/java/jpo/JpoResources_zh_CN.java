package jpo;

import java.util.*;

/*
JpoResources_cn.java:  class that holds the generic labels for the JPO application

Copyright (C) 2002-2006  Richard Eigenmann.
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or any later version. This program is distributed 
in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS 
FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
more details. You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
The license is in gpl.txt.
See http://www.gnu.org/copyleft/gpl.html for the details.

This file is edited by Franklin He (何前锋) heqf@mail.cintcm.ac.cn
Date: 21-06-2006
*/


/**
 *  class that holds the generic labels for the JPO application.
 *  Use the following command to access your Strings:
 *  Settings.jpoResources.getString("key")
 */
public class JpoResources_zh_CN extends ListResourceBundle {
	public Object[][] getContents() {
		return contents;

	}
	public static final Object[][] contents = {
		// Jpo
		{"ApplicationTitle", "JPO - 图片管理"},
		{"jpoTabbedPaneCollection", "图片目录"},
		{"jpoTabbedPaneSearches", "查找"},
		
	
		// Generic texts
         	{"genericTargetDirText", "目标文件夹："},
         	{"genericCancelText", "取消"},
		{"genericSaveButtonLabel", "保存"},
         	{"genericOKText", "确定"},             
		{"genericSelectText", "选择"},
		{"threeDotText", "..."},
		{"genericExportButtonText", "导出"},
		{"genericSecurityException", "安全引起的错误"},
		{"genericError", "错误"},
		{"internalError", "或者"},
		{"genericWarning", "警告"},
		{"genericExit", "退出"},
		{"outOfMemoryError", "内存溢出"},
		{"areYouSure", "您确认所做的操作吗？"},
		
		
		
		// Help About Dialog
		{"HelpAboutText", "JPO 版本 0.8.5 是基于Java/Swing 开发的软件\n" 
			+ "作者Richard Eigenmann在瑞士苏黎世, \n" 
			+ "版权 2000 - 2006\n"
			+ "邮件地址：richard.eigenmann@gmail.com\n"
			+ "软件主页网址：http://j-po.sourceforge.net\n"
			+ "\nExif（可交换图像文件）的抽取部分蒙Drew Noakes的帮助\n"
			+  "表的排序部分蒙Philip Milne的帮助\n\n"},
		{"HelpAboutUser", "使用者：" },
		{"HelpAboutOs", "操作系统：" },
		{"HelpAboutJvm", "Java虚拟机" },
		{"HelpAboutJvmMemory", "Java虚拟机最大内存： " },
		{"HelpAboutJvmFreeMemory", "Java虚拟机可用内存： " },


		// QueryJFrame
		{"searchDialogTitle", "查找图片"},
		{"searchDialogLabel", "查找"},
		{"searchDialogSaveResultsLabel", "保存搜索结果"},
		{"advancedFindJButtonOpen", "高级搜索条件"},
		{"advancedFindJButtonClose", "简单搜索"},
		{"noSearchResults", "没有符合条件的图片。"},
		{"lowerDateJLabel", "在期间："},
		{"dateRangeError", "日期范围指定无效"},
		


		
		// PictureViewer
		{"PictureViewerTitle", "JPO图片浏览"},
		{"PictureViewerKeycodes", "可以使用以下的快捷方式\n" 
			+ "N: 下一张\n"
			+ "P: 前一张\n"
			+ "I: 关闭显示图片信息\n"
			+ "<space>,<home>: 适合窗口大小\n"
			+ "<left>,<right>,<up>,<down>: 图片移位\n"
			+ "<PgUp>: 缩小\n"
			+ "<PgDown>: 放大\n"
			+ "1: 原始大小\n"
			+ "F: 窗口尺寸菜单\n"
			+ "M: 弹出菜单"},
		{"PictureViewerKeycodesTitle", "键盘快捷方式"},
		{"NavigationPanel", "导航工具"},
		{"fullScreenJButton.ToolTipText", "全屏"},
		{"popupMenuJButton.ToolTipText", "弹出菜单"},
		{"nextJButton.ToolTipText", "下一张图片"},
		{"previousJButton.ToolTipText", "前一张图片"},
		{"infoJButton.ToolTipText", "图片信息"},
		{"resetJButton.ToolTipText", "重置"},
		{"clockJButton.ToolTipText", "自动播放"},
		{"closeJButton.ToolTipText", "关闭窗口"},
		{"rotateLeftJButton.ToolTipText", "左旋转"},
		{"rotateRightJButton.ToolTipText", "右旋转"},
		{"PictureViewerDescriptionFont", ""},

		// Settings
		{"SettingsTitleFont", ""},
		{"SettingsCaptionFont", ""},


		// SettingsDialog Texts
		{"settingsDialogTitle", "设置"},
		
		{"browserWindowSettingsJPanel", "一般项"},
		{"languageJLabel", "语言："},
		{"autoLoadJLabelLabel", "启动加载："},
		{"logfileJCheckBoxLabel", "保存日志"},
		{"logfileJLabelLabel", "日志文件路径以及文件名："},
		{"maximiseJpoOnStartupJCheckBoxLabel", "Maximise JPO window when program starts"},
		{"saveSizeJCheckBoxLabel", "在退出时保存窗口的位置"},
		{"MainCoordinates", "主窗口坐标(横坐标/纵坐标):"},
		{"MainSize", "主窗口大小(宽度/高度):"},
		
		{"pictureViewerJPanel", "图片浏览"},
		{"maximumPictureSizeLabel", "最大图片缩放尺寸"},
		{"maxCacheLabel", "图片最大缓存"},
		{"leaveSpaceLabel", "距离底部距离"},
		{"dontEnlargeJCheckBoxLabel", "不要放大小土篇"},
		{"pictureCoordinates", "默认图片窗口坐标(横坐标/纵坐标):"},
		{"pictureSize", "默认图片窗口尺寸(宽度/高度):"},
		{"pictureViewerFastScale", "图片自动播放速度"},
		
		{"thumbnailSettingsJPanel", "缩略图"},
		{"thumbnailDirLabel", "缩略图文件夹"},
		{"keepThumbnailsJCheckBoxLabel", "保存缩略图"},
		{"maxThumbnailsLabelText", "每页显示缩略图个数"},
		{"thumbnailSizeLabel", "缩略图尺寸"},
		{"thumbnailFastScale", "缩略图播放速度"},
		{"zapThumbnails", "删除所有缩略图"},
		{"thumbnailsDeleted", "缩略图已经删除"},
		
		{"autoLoadChooserTitle", "选择启动时加载的文件"},
		{"logfileChooserTitle", "选择写入日志的文件"},
		{"thumbDirChooserTitle", "选择所略图所在的文件夹"},
		
		{"settingsError", "配置错误"},
		{"generalLogFileError", "日志文件有错误，日志功能被禁用"},
		{"thumbnailDirError", "缩略图所在文件夹出现严重错误"},

		{"userFunctionJPanel", "用户自定义功能"},
		{"userFunction1JLabel", "用户自定义功能1"},
		{"userFunction2JLabel", "用户自定义功能2"},
		{"userFunction3JLabel", "用户自定义功能3"},
		{"userFunctionNameJLabel", "名称"},
		{"userFunctionCmdJLabel", "命令："},
		{"userFunctionHelpJTextArea", "%f将被文件名代替\n%u将被图片所在URL代替"},

		{"emailJPanel", "邮件服务器"},
		{"emailJLabel", "邮件服务器详细信息"},
		{"predefinedEmailJLabel", "预先配置的邮件服务器："},
		{"emailServerJLabel", "邮件服务器："},
		{"emailPortJLabel", "端口："},
		{"emailUserJLabel", "用户名："},
		{"emailPasswordJLabel", "密码："},
		
		
		
		// Settings
		{"thumbNoExistError", "缩略图文件夹不存在。\n请选择菜单编辑|配置来正确设置缩略图文件夹\n缩略图缓存功能已被禁用。"},
		{"thumbNoWriteError", "缩略图文件夹不可写。\n请选择菜单编辑|配置来正确设置缩略图文件夹\n缩略图缓存功能已被禁用。"},
		{"thumbNoDirError", "缩略图所在不是一个文件夹\n请选择菜单编辑|配置来正确设置缩略图文件夹\n缩略图缓存功能已被禁用。"},
		{"logFileCanWriteError", "所配置的日志文件不可写。\n请选择菜单编辑|配置来正确设置日志文件\n日志功能已被禁用。"},
		{"logFileIsFileError", "所配置的日志文件不是一个文件。\n请选择菜单编辑|配置来正确设置日志文件\n日志功能已被禁用。"},
		{"generalLogFileError", "日志文件有问题，日志功能被禁用。"},
		{"cantWriteIniFile", "启动配置文件写入出错。\n"},
		{"cantReadIniFile", "读配置文件JPO.ini失败. 使用默认配置\n"},
		



		// HtmlDistillerJFrame
		{"HtmlDistillerJFrameHeading", "以HTML格式导出"},
		{"HtmlDistillerThreadTitle", "导出成HTML"}, 
		{"HtmlDistillerChooserTitle", "选择保存HTML文件夹位置"},
		{"exportHighresJCheckBox", "导出清晰图片"},
		{"linkToHighresJCheckBox", "使用当前位置链接到清晰图片"},
		{"generateDHTMLJCheckBox", "生成DHTML鼠标移动效果"},
		{"generateZipfileJCheckBox", "生成清晰图片可用于下载的压缩文件包"},
		{"picsPerRowText", "列"},
		{"thubnailSizeJLabel", "所略图尺寸"},
		{"htmlDistCrtDirError", "不能够导出到文件夹！"},
		{"htmlDistIsDirError", "这不是一个文件夹！"},
		{"htmlDistCanWriteError", "这不是一个可写的文件夹"},
		{"htmlDistIsNotEmptyWarning", "目标文件夹不为空\n点确认将继续操作但可能覆盖已有的文件。"},
		{"midresSizeJLabel", "清晰度滑尺"},
		{"midresJpgQualitySlider", "Jpg清晰度"},
		{"lowresJpgQualitySlider", "低清晰度Jpg"},
		{"jpgQualityBad", "低分辨率"},
		{"jpgQualityGood", "一般分辨率"},
		{"jpgQualityBest", "高清晰"},
		// HtmlDistillerThread
		{"LinkToJpo", "制作工具：<A HREF=\"http://j-po.sourceforge.net\">JPO</A>"},
		{"htmlDistillerInterrupt", "操作已被停止"},
		{"CssCopyError", "不能拷贝样式文件jpo.css\n"},
		{"HtmlDistillerPreviewFont", ""},

				

		// ReconcileJFrame
		{"ReconcileJFrameTitle", "图片集与文件夹相应文件检查"},
		{"ReconcileBlaBlaLabel", "<HTML>检查文件夹中的文件是否出现在图片集中</HTML>"},
		{"directoryJLabelLabel", "需要检查的文件夹"},
		{"directoryCheckerChooserTitle", "选择需要检查的文件夹"},
		{"ReconcileFound", "在图片集中监测到的文件"},
		{"ReconcileNotFound", "不在图片集中的有： "},
		{"ReconcileDone", "完成检查\n"},
		{"ReconcileInterrupted", "检查被中断\n"},
		{"ReconcileListPositives", "列出相匹配的"},
		{"ReconcileOkButtonLabel", "匹配检查"},
		{"ReconcileSubdirectories", "匹配检查所有子文件夹"},
		{"ReconcileCantReadError", "不能够读 "},
		{"ReconcileNullFileError", "文件夹不正确"},
		{"ReconcileStart", "匹配检查文件夹"},
		{"ReconcileNoFiles", "没有发现文件\n"},

		
		// CollectionDistillerJFrame
		{"CollectionDistillerJFrameFrameHeading", "导成新的图片集"},
		{"collectionExportPicturesText", "导出图片"},
		{"xmlFileNameLabel", "XML文件名称："},
		{"collectionExportChooserTitle", "选择导出到文件夹"},


		// ConsolidateGroupJFrame
		{"highresTargetDirJTextField", "选择清晰图片整合目录"},
		{"lowresTargetDirJTextField", "选择低分辨率图片整合目录"},
		{"RecurseSubgroupsLabel", "对所有的子目录操作"},
		{"ConsolidateGroupBlaBlaLabel", "<HTML>此功能将所有选中的图片组移动到指定的文件夹。这将自动修复文件组中的图片引用位置，所有的文件会在磁盘上移动<br><p> <font color=red>您确认要这样操作吗？<br></font></htm>"},
		{"ConsolidateGroupJFrameHeading", "整合/移动图片"},
		{"ConsolidateButton", "整合"},
		{"ConsolidateFailure", "图片整合失败，退出"},
		{"ConsolitdateProgBarTitle", "图片正在整合中"},
		{"ConsolitdateProgBarDone", "图片集整合完毕"},
		{"lowresJCheckBox", "连同低分辨率图片也整合"},
		

		
		// JarDistillerJFrame
		{"groupExportJarTitleText", "导出成Jar格式的包"},
		{"JarDistillerLabel", "创建Jar (Java包)："},
		{"SelectJarFileTitle", "选择Jar包导出的文件夹位置："},
		
		// PictureInfoEditor
		{"PictureInfoEditorHeading", "图片属性"},
		{"highresChooserTitle", "选择高清晰度图片"},
		{"pictureDescriptionLabel", "图片名称："},
		{"creationTimeLabel", "创建时间："},
		{"highresLocationLabel", "高清晰度图片位置："},
		{"lowresLocationLabel", "低清晰度图片位置；"},
		{"filmReferenceLabel", "影像位置："},
		{"rotationLabel", "在打开时旋转图片："},
		{"commentLabel", "描述："},
		{"copyrightHolderLabel", "版权信息："},
		{"photographerLabel", "摄影作者："},
		{"resetLabel", "重置"},
		{"checksumJButton", "刷新"},
		{"checksumJLabel", "Adler32校检和"},
		{"parsedAs", "解析成： "},
		{"failedToParse", "不能解析日期"},
		{"categoriesJLabel-2", "类目："},
		{"setupCategories", ">>设置类目<<"},
		{"noCategories", ">> 不属于任何类目 <<"},

		
		//GroupInfoEditor
		{"GroupInfoEditorHeading", "编辑图片组描述"},
		{"groupDescriptionLabel", "图片组描述"},
		
		// GroupPopupMenu
		{"groupShowJMenuItem", "显示图片组"},
		{"groupSlideshowJMenuItem", "显示图片"},
		{"groupFindJMenuItemLabel", "查找"},
		{"groupEditJMenuItem", "重命名"},
		{"groupRefreshJMenuItem", "刷新图标"},
		{"groupTableJMenuItemLabel", "以表格方式编辑"},
		{"addGroupJMenuLabel", "添加图片组"},
		{"addNewGroupJMenuItemLabel", "新建图片组"},
		{"addPicturesJMenuItemLabel", "图片"},
		{"addCollectionJMenuItemLabel", "图片集"},
		{"groupExportNewCollectionMenuText", "导出成图片集"},
		{"addFlatFileJMenuItemLabel", "无格式文件"},
		{"moveNodeJMenuLabel", "移动"},
		{"moveGroupToTopJMenuItem", "置顶"},
		{"moveGroupUpJMenuItem", "向上"},
		{"moveGroupDownJMenuItem", "向下"},
		{"moveGroupToBottomJMenuItem", "置底"},
		{"indentJMenuItem", "相内"},
		{"outdentJMenuItem", "向外"},
		{"groupRemoveLabel", "移除"},
		{"consolidateMoveLabel", "整合/移动"},
		{"sortJMenu", "排序方式"},
		{"sortByDescriptionJMenuItem", "名称"},
		{"sortByFilmReferenceJMenuItem", "影像位置"},
		{"sortByCreationTimeJMenuItem", "创建日期"},
		{"sortByCommentJMenuItem", "描述"},
		{"sortByPhotographerJMenuItem", "摄影作者"},
		{"sortByCopyrightHolderTimeJMenuItem", "版权"},
		{"groupExportHtmlMenuText", "导出成HTML"},
		{"groupExportFlatFileMenuText", "导出成无格式文件"},
		{"groupExportJarMenuText", "导出成Jar压缩包"},
		
		
		// PicturePopupMenu
		{"pictureShowJMenuItemLabel", "显示图片"},
		{"pictureEditJMenuItemLabel", "属性"},
		{"copyImageJMenuLabel", "复制图片"},
		{"copyToNewLocationJMenuItem", "选择复制的文件夹位置"},
		{"FileOperations", "文件操作"},
		{"fileRenameJMenuItem", "重命名"},
		{"FileRenameLabel1", "重命名 \n"},
		{"FileRenameLabel2", "\n成: "},
		{"fileDeleteJMenuItem", "删除"},
		{"pictureRefreshJMenuItem", "刷新缩略图"},
		{"pictureMailSelectJMenuItem", "选中发送邮件"},
		{"pictureMailUnselectJMenuItem", "选择不用来发送邮件"},
		{"pictureMailUnselectAllJMenuItem", "清除要发送邮件的选择"},
		{"rotation", "翻转"},
		{"rotate90", "右转90度"},
		{"rotate180", "旋转180度"},
		{"rotate270", "左转270度"},
		{"rotate0", "不旋转"},
		{"userFunctionsJMenu", "用户自定义动能"},
		{"pictureNodeRemove", "删除图片"},
		{"movePictureToTopJMenuItem", "置顶"},
		{"movePictureUpJMenuItem", "向上移动"},
		{"movePictureDownJMenuItem", "向下移动"},
		{"movePictureToBottomJMenuItem", "置底"},
		{"recentDropNodePrefix", "移到图组： "},
		{"categoryUsagetJMenuItem", "类目"},


		// ThumbnailJScrollPane
		{"ThumbnailSearchResults", "搜索结果"},
		{"ThumbnailSearchResults2", "在"},
		{"ThumbnailToolTipPrevious", "前一页"},
		{"ThumbnailToolTipNext", "后一页"},
		{"ThumbnailJScrollPanePage", "页"},

		//ChangeWindowPopupMenu
		{"fullScreenLabel", "全屏"},
		{"leftWindowLabel", "左"},
		{"rightWindowLabel", "右"},
		{"topLeftWindowLabel", "左上"},
		{"topRightWindowLabel", "右上"},
		{"bottomLeftWindowLabel", "左下"},
		{"bottomRightWindowLabel", "右下"},
		{"defaultWindowLabel", "默认"},
		{"windowDecorationsLabel", "窗体"},
		{"windowNoDecorationsLabel", "没有窗体"},


		// CollectionJTree
		{"DefaultRootNodeText", "新建图片集"},
		{"CopyImageDialogButton", "复制"},
		{"CopyImageDialogTitle", "指定复制的位置："},
		{"CopyImageNullError", "复制文件夹的参数无效，复制终止。"},
		{"CopyImageDirError", "不能创建目标文件夹，复制终止。\n"},
		{"fileOpenButtonText", "打开"},
		{"fileOpenHeading", "打开图片集"},
		{"fileSaveAsTitle", "另存为图片集"},
		{"collectionSaveTitle", "保存图片集"},
		{"collectionSaveBody", "另存图片集\n"},
		{"addSinglePictureTitle", "选择需要添加的图片"},
		{"addSinglePictureButtonLabel", "选择"},
		{"addFlatFileTitle", "选择无格式图片文件列表"},
		{"saveFlatFileTitle", "以无格式方式保存图片集"},
		{"saveFlatFileButtonLabel", "保存"},
		{"moveNodeError", "移动目标在同一文件夹下，移动无效。"},
		{"unsavedChanges", "不保存退出。"},
		{"confirmSaveAs", "文件已经存在\n要覆盖保存吗？"},
		{"discardChanges", "放弃"},
		{"noPicsForSlideshow", "图片组中没有图片"},
		{"fileRenameTitle", "重命名文件"},
		{"fileDeleteTitle", "删除文件"},
		{"fileDeleteError", "不能够删除文件\n"},
		{"deleteRootNodeError", "根图片集不能被删除"},
				
		// ApplicationJMenuBar
		{"FileMenuText", "文件"},
		{"FileNewJMenuItem", "新建图片集"},
		{"FileLoadMenuItemText", "打开图片集"},
		{"FileOpenRecentItemText", "打开最近使用文件"},
		{"FileAddMenuItemText", "添加图片"},
		{"FileCameraJMenuItem", "从相机中添加图片"},
		{"FileSaveMenuItemText", "保存图片集"},
		{"FileSaveAsMenuItemText", "另存为"},
		{"FileExitMenuItemText", "退出"},
		{"EditJMenuText", "编辑"},
		{"EditFindJMenuItemText", "查找"},
		{"EditCheckDirectoriesJMenuItemText", "检查"},
		{"EditCollectionPropertiesJMenuItem", "图片集属性"},
		{"EditCheckIntegrityJMenuItem", "完整性检查"},
		{"EditCamerasJMenuItem", "相机"},
		{"EditCategoriesJMenuItem", "类目"},
		{"EditSettingsMenuItemText", "设置..."},
		{"actionJMenu", "工具"},
		{"emailJMenuItem", "发送邮件"},
		{"HelpJMenuText", "帮助"},
		{"HelpAboutMenuItemText", "关于"},
		{"HelpLicenseMenuItemText", "授权"},
		
		// PictureViewer
		{"autoAdvanceDialogTitle", "自动播放"},
		{"randomAdvanceJRadioButtonLabel", "任意顺序播放"},
		{"sequentialAdvanceJRadioButtonLabel", "顺序播放"},
		{"restrictToGroupJRadioButtonLabel", "限制在本图片组内播放"},
		{"useAllPicturesJRadioButtonLabel", "循环播放所有图片"},
		{"timerSecondsJLabelLabel", "播放时延(秒)"},

		// ExifViewerJFrame		
		{"ExifTitle", "EXIF图片交换格式头\n"},
		{"noExifTags", "没有发现EXIF图片交换格式标签"},
		
		// PictureAdder
		{"PictureAdderDialogTitle", "添加图片以及文件夹"},
		{"PictureAdderProgressDialogTitle", "正在添加图片"},
		{"notADir", "不是一个文件夹\n"},
		{"notGroupInfo", "不是图片组"},
		{"fileChooserAddButtonLabel", "添加"},
		{"recurseSubdirectoriesTitle", "应用于所有的子文件夹"},
		{"recurseSubdirectoriesMessage", "您的选择包含了子文件夹，\n将所有的子文件夹也加入吗？"},
		{"recurseSubdirectoriesOk", "添加"},
		{"recurseSubdirectoriesNo", "否"},
		{"picturesAdded", "图片已经添加"},
		{"pictureAdderOptionsTab", "选项"},
		{"pictureAdderThumbnailTab", "所略图"},
		{"pictureAdderCategoryTab", "类目"},

		// AddFromCamera
		{"AddFromCamera", "从相机中添加图片"},
		{"cameraNameJLabel", "相机名称："},
		{"cameraDirJLabel", "相机在文件系统中的位置："},
		{"cameraConnectJLabel", "连接相机的命令："},
		{"cameraDisconnectJLabel", "断开相机连接的命令："},
		{"allPicturesJRadioButton", "把相机中的所有图片都加入图片集"},
		{"newPicturesJRadioButton", "只从相机中添加新的图片"},
		{"missingPicturesJRadioButton", "添加不在图片集中的文件"},
		{"targetDirJLabel", "图片的目标文件夹："},
		{"AddFromCameraOkJButton", "运行"},
		{"editCameraJButton", "编辑相机"},
		{"categoriesJButton", "类目"},
		
		// CameraEditor
		{"CameraEditor", "编辑相机设置"},
		{"cameraNewNameJLabel", "新建相机名"},
		{"runConnectJButton", "运行"},
		{"saveJButton", "保存"},
		{"memorisedPicsJLabel", "上次导入的图片数："},
		{"refreshJButton", "刷新"},
		{"zeroJButton", "取消"},
		{"addJButton", "添加"},
		{"deleteJButton", "删除"},
		{"closeJButton", "关闭"},
		{"filenameJCheckBox", "以文件名方式记忆图片"},
		{"refreshJButtonError", "请先保存您的变更！"},
		
		

		// Camera
		{"countingChecksum", "正在创建校检和"},
		{"countingChecksumComplete", "计算完成校检和"},
		{"newCamera", "新建相机"},
		
		
		

		// XmlDistiller
		{"DtdCopyError", "不能够复制collection.dtd配置文件\n"},

		// CollectionProperties
		{"CollectionPropertiesJFrameTitle", "图片集属性"},
		{"CollectionNodeCountLabel", "图片及图组总数："},
		{"CollectionGroupCountLabel", "图组数："},
		{"CollectionPictureCountLabel", "图片数： "},
		{"CollectionSizeJLabel", "占用磁盘空间："},
		{"queCountJLabel", "待处理的缩略图数: "},
		{"editProtectJCheckBoxLabel", "图片集写保护"},
		
		// Tools
		{"copyPictureError1", "不能复制\n"},
		{"copyPictureError2", "\n到： "},
		{"copyPictureError3", "\n原因： "},
		{"freeMemory", "可用内存空间： "},

		// PictureAdder
		{"recurseJCheckBox", "应用到所有子文件夹"},
		{"retainDirectoriesJCheckBox", "保持目录结构"},
		{"newOnlyJCheckBox", "只添加新的图片"},
		{"showThumbnailJCheckBox", "显示缩略图"},

		// IntegrityChecker
		{"IntegrityCheckerTitle", "检查图片集完整性"},
		{"integrityCheckerLabel", "正在检查完整性"},
		{"check1", "检查日期格式"},
		{"check1done", "日期格式不正确"},
		{"check2", "校检和检验"},
		{"check2progress", "正在运行校检和修复: "},
		{"check2done", "修复后的校检和 "},
		{"check3", "检查3"},
		
		// SortableDefaultMutableTreeNode
		{"GDPMdropBefore", "拖放在目标前"},
		{"GDPMdropAfter", "拖放在目标后"},
		{"GDPMdropIntoFirst", "拖放到最顶端"},
		{"GDPMdropIntoLast", "拖放到底部"},
		{"GDPMdropCancel", "取消拖放"},
		{"copyAddPicturesNoPicturesError", "没有找到图片，操作退出。"},
		{"FileDeleteTitle", "删除"},
		{"FileDeleteLabel", "删除文件\n"},
		{"newGroup", "新建图组"},
		{"queriesTreeModelRootNode", "查找"},


		// CategoryEditorJFrame
		{"CategoryEditorJFrameTitle", "类目编辑器"},
		{"categoryJLabel", "类目"},
		{"categoriesJLabel", "类目"},
		{"addCateogryJButton", "添加类目"},
		{"deleteCateogryJButton", "删除类目"},
		{"renameCateogryJButton", "重命名类目"},
		{"doneJButton", "完成"},
		{"countCategoryUsageWarning1", "共计"},
		{"countCategoryUsageWarning2", "还有属于该类的图片\n确信要删除该类吗？"},

		// CategoryUsageJFrame
		{"CategoryUsageJFrameTitle", "类使用状况"},
		{"numberOfPicturesJLabel", "图片被选中"},
		{"updateJButton", "更新"},
		{"refreshJButtonCUJF", "刷新"},
		{"modifyCateogryJButton", "类目"},
		{"cancelJButton", "取消"},

		
		// EmailerJFrame
		{"EmailerJFrame", "发送邮件"},
		{"imagesCountJLabel", "选中的图片数： "},
		{"emailJButton", "发送"},
		{"noNodesSelected", "没有选中的图片，请在相应图片上按右键弹出菜单来选择"},
		{"fromJLabel", "从："},
		{"toJLabel", "到："},
		{"messageJLabel", "信息："},
		{"subjectJLabel", "主题："},
		{"emailSendError", "发生了如下的错误：\n"},
		{"emailOK", "邮件发送成功"},
		{"emailSizesJLabel", "邮件大小："},
		{"emailResizeJLabel", "邮件大小变成："},
		{"emailSize1", "小图片 (350 x 300)"},
		{"emailSize2", "中等图片 (700 x 550)"},
		{"emailSize3", "原始图片大小"},
		{"emailSize4", "大图片大小 (1000 x 800)"},
		{"emailSize5", "原始大小"},
		{"emailOriginals", "附上原始图片"},
		{"emailNoNodes", "没有需要发送的图片，请用右弹出菜单在相应图片上选择"},
		{"emailNoServer", "没有配置邮件服务器。在 编辑 > 设置... > 邮件服务器 选项中配置服务器。"},

		//Emailer Thread
		{"EmailerLoading", "正在装入："},
		{"EmailerScaling", "正在改变大小： "},
		{"EmailerWriting", "正在写入"},
		{"EmailerAdding", "正在添加"},
		{"EmailerSending", "正在发送邮件"},
		{"EmailerSent", "邮件已经分发到服务器"},

		//CategoryQuery
		{"CategoryQuery", "类目查询"},

		//PicturePanel
		{"PicturePaneInfoFont", ""},   
		{"PicturePaneSize", "尺寸："}, 
		{"PicturePaneMidpoint", " 中心点"}, 
		{"PicturePaneLoadTime", "载入："}, 
		{"PicturePaneSeconds", "秒"}, 
		{"PicturePaneFreeMemory", "可用內存空间："}, 
		{"PicturePaneReadyStatus", "就绪"},

		//ExifInfo
		{"ExifInfoCamera", "照相机："},
		{"ExifInfoLens", "镜头："},
		{"ExifInfoShutterSpeed", "快门速度："},
		{"ExifInfoAperture", "光圈："},
		{"ExifInfoFocalLength", "焦距："},
		{"ExifInfoISO", "感光度ISO："},
		{"ExifInfoTimeStamp", "拍摄时间："},

		//ThumbnailDescriptionJPane
		{"ThumbnailDescriptionJPanelLargeFont", ""},
		{"ThumbnailDescriptionJPanelSmallFont", ""},
		{"ThumbnailDescriptionNoNodeError", "此位置上没有图片。"},

		// ScalablePicture
		{"ScalablePictureUninitialisedStatus", "没有初始化"},
		{"ScalablePictureLoadingStatus", "正在装入"},
		{"ScalablePictureRotatingStatus", "正在翻转"},
		{"ScalablePictureScalingStatus", "正在缩放"},


		{"Template", "模板"}
		
		
	};
}

