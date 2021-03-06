package wisoft.pack.edits;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.wb.swt.ResourceManager;

import wisoft.pack.dialogs.UpdateServerDialog_EditConf;
import wisoft.pack.models.FileModel;
import wisoft.pack.models.Model;
import wisoft.pack.models.PackConfig_Server;
import wisoft.pack.models.PackInfoModel;
import wisoft.pack.utils.FileUtil;
import wisoft.pack.utils.PackConfigInfo;
import wisoft.pack.utils.StringUtils;
import wisoft.pack.utils.UpdateInfo;

public class FFormPage extends FormPage {
	
	private Table table;
	public Text txtNewText;
	Vector<ProgressBar> bars = new Vector<ProgressBar>(); 
	private PackInfoModel pm;
	private boolean isstop = false;//是否中断
	private PackConfig_Server selSever;//当前更新的服务器
	Map<PackConfig_Server,TableItem> servers = new HashMap<PackConfig_Server,TableItem>() ;
	Map<TableItem,Button> buttons = new HashMap<TableItem,Button>() ;
	
	private Shell shell;
	
	/**
	 * Create the form page.
	 * @param id
	 * @param title
	 */
	public FFormPage(String id, String title) {
		super(id, title);
	}

	/**
	 * Create the form page.
	 * @param editor
	 * @param id
	 * @param title
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter id "Some id"
	 * @wbp.eval.method.parameter title "Some title"
	 */
	public FFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
		pm =((PackInfoInput)editor.getEditorInput()).getPackinfo();
		
		shell = Display.getDefault().getActiveShell();
	}

	/**
	 * Create contents of the form.
	 * @param managedForm
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		form.setText("更新过程");
		Composite body = form.getBody();
		toolkit.decorateFormHeading(form.getForm());
		toolkit.paintBordersFor(body);
		managedForm.getForm().getBody().setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(managedForm.getForm().getBody(), SWT.NONE);
		managedForm.getToolkit().adapt(composite);
		managedForm.getToolkit().paintBordersFor(composite);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(composite, SWT.BORDER | SWT.SMOOTH);
		sashForm.setSashWidth(2);
		sashForm.setOrientation(SWT.VERTICAL);
		managedForm.getToolkit().adapt(sashForm);
		managedForm.getToolkit().paintBordersFor(sashForm);
		
		Composite composite_1 = new Composite(sashForm, SWT.NONE);
		managedForm.getToolkit().adapt(composite_1);
		managedForm.getToolkit().paintBordersFor(composite_1);
		composite_1.setLayout(new GridLayout(4, false));
		
		table = new Table(composite_1, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.MULTI);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 2));
		managedForm.getToolkit().adapt(table);
		managedForm.getToolkit().paintBordersFor(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setText("\u670D\u52A1\u5668\u540D");
		tableColumn.setWidth(130);
		
		TableColumn tableColumn_2 = new TableColumn(table, SWT.NONE);
		tableColumn_2.setWidth(90);
		tableColumn_2.setText("\u4E0A\u6B21\u66F4\u65B0\u65F6\u95F4");
		
		TableColumn tableColumn_3 = new TableColumn(table, SWT.NONE);
		tableColumn_3.setWidth(80);
		tableColumn_3.setText("\u6587\u4EF6\u8FDB\u5EA6");
		
		TableColumn tableColumn_1 = new TableColumn(table, SWT.NONE);
		tableColumn_1.setWidth(60);
		tableColumn_1.setText("\u624B\u52A8\u914D\u7F6E");
		
		ImageHyperlink mghprlnkNewImagehyperlink = managedForm.getToolkit().createImageHyperlink(composite_1, SWT.NONE);
		mghprlnkNewImagehyperlink.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2));
		mghprlnkNewImagehyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
						updateSchedule();
			}
		});
		mghprlnkNewImagehyperlink.setImage(ResourceManager.getPluginImage("WiosftUpdatePack", "icons/btn_deploy_normal.png"));
		mghprlnkNewImagehyperlink.setHoverImage(ResourceManager.getPluginImage("WiosftUpdatePack", "icons/btn_deploy_over.png"));
		managedForm.getToolkit().paintBordersFor(mghprlnkNewImagehyperlink);
		mghprlnkNewImagehyperlink.setText("");
		
		Composite composite_2 = new Composite(sashForm, SWT.NONE);
		managedForm.getToolkit().adapt(composite_2);
		managedForm.getToolkit().paintBordersFor(composite_2);
		composite_2.setLayout(new FormLayout());
		
		Composite composite_3 = new Composite(composite_2, SWT.NONE);
		FormData fd_composite_3 = new FormData();
		fd_composite_3.bottom = new FormAttachment(0, 30);
		fd_composite_3.right = new FormAttachment(100);
		fd_composite_3.top = new FormAttachment(0);
		fd_composite_3.left = new FormAttachment(0);
		composite_3.setLayoutData(fd_composite_3);
		managedForm.getToolkit().adapt(composite_3);
		managedForm.getToolkit().paintBordersFor(composite_3);
		composite_3.setLayout(new GridLayout(2, false));
		
		Label label = new Label(composite_3, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		managedForm.getToolkit().adapt(label, true, true);
		label.setText("\u66F4\u65B0\u65E5\u5FD7\uFF1A");
		
		
		Button button = new Button(composite_3, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell,SWT.SAVE);
				dialog.setFilterExtensions(new String[]{".log",".txt"});
				String fileName = dialog.open();
				//FileWriter fw = new File
			}
		});
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		managedForm.getToolkit().adapt(button, true, true);
		button.setText("\u4FDD\u5B58\u2026\u2026");
		
		txtNewText = managedForm.getToolkit().createText(composite_2, "", SWT.READ_ONLY | SWT.V_SCROLL | SWT.MULTI);
		FormData fd_txtNewText = new FormData();
		fd_txtNewText.top = new FormAttachment(composite_3, 6);
		fd_txtNewText.bottom = new FormAttachment(100);
		fd_txtNewText.right = new FormAttachment(composite_3, 0, SWT.RIGHT);
		fd_txtNewText.left = new FormAttachment(composite_3, 0, SWT.LEFT);
		txtNewText.setLayoutData(fd_txtNewText);
		sashForm.setWeights(new int[] {15, 30});
		//servers = PackConfigInfo.getInstance().getUnPackProInfos();
		getServerList();
	}
	
	
	
	private void getServerList()
	{
		table.clearAll();
		bars.clear();
		buttons.clear();
		for(final PackConfig_Server server:PackConfigInfo.getInstance().getUnPackProInfos())
		{
			TableItem item = new TableItem(table, SWT.NULL);
			
			item.setText(0,server.getServerName());
			String updateTime = server.getPackUpdateTime(this.pm.getPackageinfo()) ;
			item.setText(1,updateTime);
			boolean isUpdated = StringUtils.checkStrFormat(updateTime);
			
			TableEditor editor = new TableEditor(table);
			editor.grabHorizontal = editor.grabVertical = true;
			ProgressBar bar = new ProgressBar(table, SWT.NONE);
			bar.setMaximum(pm.getUpdateFileRoot().countFiles());
			if(isUpdated) bar.setSelection(pm.getUpdateFileRoot().countFiles());
			editor.setEditor(bar, item, 2);
			
			TableEditor editor_1 = new TableEditor(table);
			editor_1.grabHorizontal = editor_1.grabVertical = true;
			Button innerbutton = new Button(table, SWT.NONE);
			if(isUpdated)
			{
				innerbutton.setText("已配置");
				innerbutton.setEnabled(true);
			}
			else
			{
				innerbutton.setText("未配置");
				innerbutton.setEnabled(false);//文件更新前设置按钮为false；
			}
			innerbutton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					UpdateServerDialog_EditConf edit =new UpdateServerDialog_EditConf(shell,pm.getConfFiles());
					edit.setServer(server);
					edit.open();
				}
			});
			editor_1.setEditor(innerbutton, item, 3);
			//editors.add(editor_1);
	        bars.add(bar);
	        buttons.put(item, innerbutton);
	        item.setData(server);
		}
	}
	
	
	private void getTableCheckedItem()
	{
		servers.clear();
		for(TableItem item : this.table.getItems())
		{
			if(item.getChecked())
				servers.put((PackConfig_Server)item.getData(), item);
		}
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-ss-mm");
	File logfile = null;
	FileWriter fw  = null;
	private void updateSchedule()
	{
		//
		String logdate = sdf.format(new Date());
		logfile = new File(pm.getSavePath()+File.separator+"UpdateLog_"+logdate+".log");
		try {
			fw = new FileWriter(logfile,true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//更新前，检查更新包
		print("【检查】更新包完整性----"+pm.getName(),true );
		print("   需要更新"+pm.getUpdateFileRoot().countFiles()+"个文件",false);
		int confnum = pm.getConfFiles().size();
		print("   需要配置"+confnum+"个文件",false);
		getTableCheckedItem();
		new Thread() {
			public void run() {
		for(PackConfig_Server item:servers.keySet())
		{
			//获取要更新的服务器
			selSever = item;
			print("正在更新到服务器："+selSever.getServerName(),true);
			print("【检查】服务文件夹是否存在----"+selSever.getWebappPath(),true );
			
			Display.getDefault().syncExec(new Runnable() {   
				//这个线程是调用UI线程控件
				public void run() {   						
					int i = table.indexOf(servers.get(selSever));
					ProgressBar bar =bars.get(i);
					bar.setSelection(0);
				}
			});
			
			try
			{
				File server = new File(selSever.getWebappPath());
				if(!server.exists()&&!server.isDirectory())
				{
					print("【错误】：更新指向的服务器WEBAPP文件夹不存在！来自服务："+selSever.getServerName(),true );
					return;
				}
			}
			catch(Exception e)
			{
				print("【错误】：检查服务器发生问题！来自："+e.getStackTrace().toString(),true );
				return;
			}
			print("检查通过服务器WEBAPP地址："+selSever.getWebappPath(),false);
			boolean b = openDialog(MessageDialog.CONFIRM, "开始更新？","所有检查完毕，确定启动更新程序？");
			if(!b)
			{
				print("【中断】：用户已取消！",true);
				return;
			}
			copyCircle(pm.getUpdateFileRoot());
			print("【复制完成】：更新文件全部复制完成，弹出手动处理文件开始……",true);
			
			Display.getDefault().syncExec(new Runnable() {   
				//这个线程是调用UI线程控件
				public void run() {   
					UpdateServerDialog_EditConf edit =new UpdateServerDialog_EditConf(shell,pm.getConfFiles());
					edit.setServer(selSever);
					if(IDialogConstants.OK_ID!=edit.open())
						print("【中断】：用户取消手动文件更新！",true);
					else
						buttons.get(servers.get(selSever)).setText("已配置");
				}
			});
			doExecuteSql();
			selSever.setPackVersionRecord(pm.getPackageinfo());
			Display.getDefault().syncExec(new Runnable() {   
				//这个线程是调用UI线程控件
				public void run() {   						
					TableItem item = servers.get(selSever);
					item.setText(1,selSever.getPackUpdateTime(pm.getPackageinfo()));
					buttons.get(item).setEnabled(true);
				}
			});
		}
		
			}
		}.start();
	}
	
	/**文件复制循环体
	 * @param file
	 */
	private void copyCircle(FileModel file )
	{
		if(isstop)
		{
			print("【中断】：用户已取消！",true);
			return;
		}
		File serverfile = new File(selSever.getWebappPath()+file.getFullPath());
		if(file.isDir())
		{
			if(!file.isVirtual())
			{
				if(serverfile.exists())
					print("【合并文件夹】："+serverfile.getAbsolutePath(),false);
				else
					print("【创建文件夹】："+serverfile.getAbsolutePath(),false);
				serverfile.mkdirs();
			}
			if(file.isConf()&&file.getConftype()!=null)
			{
				print("【文件夹配置】："+serverfile.getAbsolutePath()+"\n[配置类型]"+file.getConftype()+"\n[配置说明]"+file.getContent(),false);
			}
		}
		else
		{
			try
			{
				File packfile = new File(pm.getSavePath()+File.separator+UpdateInfo.UpdateDirName+file.getFullPath());
				if(packfile.exists())
				{
					FileUtil.copyFile(packfile, serverfile);
					print("【复制文件】："+file.getFullPath(),false);
				}
				if(file.isConf())
				{
					print("【手动配置】："+serverfile.getAbsolutePath()+"\n[配置说明]"+file.getContent()+"\n[配置类型]"+file.getConftype(),false);
				}
			}
			catch(Exception e)
			{
				print("【错误】：更新文件"+file.getFullPath()+"出错。来自："+e.getStackTrace().toString(),true );
			}
		}
		updateprogressbar();
		for(Model zfile:file.getChildren())
		{
			copyCircle((FileModel)zfile);
		}
	}
	
	public void doExecuteSql()
	{
		if(isstop)
			return;
		print("【执行SQL】:开始",true);
		String sql = "SQLPLUS "+selSever.getDBPath()+" @"+pm.getSavePath()+File.separator+UpdateInfo.SqlFileName;
        try {
        	final Process proc =Runtime.getRuntime().exec(sql);
        	//读取打印数据流
        	proc.getOutputStream().close();
			final BufferedReader pin = new BufferedReader(
		              new InputStreamReader(proc.getInputStream()));
		    Thread errReadThread = new Thread() {
	            public void run() {
	              try {
	                String line=null;
	                while ( (line=pin.readLine()) != null) {
	                	System.out.println(line);
	                	print(line,false);
	                }
	                proc.waitFor();
	                pin.close();
	              }
	              catch (Exception ex) {
	                ex.printStackTrace();
	              }
	            }
	          };
	          errReadThread.start();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
	}
	
	
	/** 更新进度条
	 */
	private synchronized void  updateprogressbar()
	{
		Display.getDefault().syncExec(new Runnable() {   
			//这个线程是调用UI线程控件
			public void run() {   						
					try {
					Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					int i = table.indexOf(servers.get(selSever));
					ProgressBar bar =bars.get(i);
					bar.setSelection(bar.getSelection()+1);
				}
		});
	}
	
	
	
	private boolean openDialog(int type,final String title,final String content)
	{
		final OpenDialogWrapper ow = new OpenDialogWrapper();
		Display.getDefault().syncExec(new Runnable() {   
			//这个线程是调用UI线程控件
			public void run() {   	
				ow.uiValue = MessageDialog.openQuestion(shell,title,content);
			}
		});
		return ow.uiValue;
	}
	
	/**
	 * 显示数据到text控件，为了防止界面假死，必须另起线程，异步调度执行线程
	 * */
	private synchronized void print(final String str,final boolean isShowTime)
	{
		final SimpleDateFormat sf =  new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		Display.getDefault().syncExec(new Runnable() {   
			//这个线程是调用UI线程控件
			public void run() {   
				
				if(isShowTime)
				{
					txtNewText.append(sf.format(new Date())+": "+str+"\n");
					try {
						fw.write(sf.format(new Date())+": "+str+"\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					txtNewText.append(str+"\n");
					try {
						fw.write(str+"\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}   
		});   
	}
	
}

class OpenDialogWrapper {
	   public boolean uiValue;
	}
