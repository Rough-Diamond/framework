/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.tools.projectgen;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import jp.rough_diamond.commons.lang.StringUtils;
import jp.rough_diamond.tools.projectgen.ProjectGeneratorParameter.ApplicationOption;
import jp.rough_diamond.tools.projectgen.ProjectGeneratorParameter.ApplicationType;

/**
 *　プロジェクト生成UI
 */
public class ProjectGeneratorGUI {
	private ProjectGeneratorParameter param = new ProjectGeneratorParameter();
	private JFrame frame;
	
	public static void main(String[] args) {
		new ProjectGeneratorGUI().start();
	}

	void start() {
		createDialog().setVisible(true);
	}
	
	JFrame createDialog() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("新規プロジェクトの作成");
		Box topBox = Box.createVerticalBox();
		Container panel = frame.getContentPane();
		panel.setLayout(new BorderLayout());
		panel.add(topBox, BorderLayout.CENTER);
		topBox.add(makeProjectNamePanel());
		topBox.add(makeFrameworkRootPanel());
		topBox.add(makeProjectRootPanel());
		topBox.add(makeEncodingPanel());
		topBox.add(makeApplicationTypePanel());
		topBox.add(makeApplicationOptionPanel());
		topBox.add(makeButtonPanel());
		frame.pack();
		return frame;
	}
	
	Component makeButtonPanel() {
		Box box = Box.createHorizontalBox();
		JButton okButton = new JButton("プロジェクト作成");
		box.add(okButton);
		okButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				makeProject();
			}
		});
		JButton cancelButton = new JButton("キャンセル");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				finish();
			}
		});
		box.add(cancelButton);
		return box;
	}

	Component makeApplicationOptionPanel() {
		Box box = Box.createHorizontalBox();
		box.setBorder(new TitledBorder("オプション"));
		final JCheckBox cb1 = new JCheckBox("makeBeanコマンドを使用する");
		cb1.setModel(new OptionModel(ApplicationOption.USING_MAKE_BEAN));
		box.add(cb1);
		final JCheckBox cb2 = new JCheckBox("ESBを使用する");
		box.add(cb2);
		cb2.setModel(new OptionModel(ApplicationOption.USING_ESB));
		final JCheckBox cb3 = new JCheckBox("RDBMSを使用する");
		box.add(cb3);
		cb3.setModel(new OptionModel(ApplicationOption.USING_DATABASE));
		final JCheckBox cb4 = new JCheckBox("WebFrameworkを使用する");
		cb4.setModel(new OptionModel(ApplicationOption.USING_RDF_WEB_FR));
		box.add(cb4);
		cb4.setEnabled(false);
		paramObservable.addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				if(!(arg instanceof ProjectGeneratorParameter)) {
					return;
				}
				if(param.getAppType() == ApplicationType.SIMPLE) {
					cb4.setEnabled(false);
					cb4.setSelected(false);
				} else {
					cb4.setEnabled(true);
				}
			}
		});
		return box;
	}

	class OptionModel extends JToggleButton.ToggleButtonModel {
		private static final long serialVersionUID = 1L;
		private ApplicationOption option;
		OptionModel(ApplicationOption option) {
			this.option = option;
		}
		@Override
		public void setSelected(boolean b) {
			super.setSelected(b);
			Set<ApplicationOption> set = param.getOptions();
			boolean isChange;
			if(b) {
				isChange = set.add(option);
			} else {
				isChange = set.remove(option);
			}
			if(isChange) {
				ApplicationOption[] list = set.toArray(new ApplicationOption[set.size()]);
				param.setOptions(list);
				paramObservable.setChanged();
			}
		}
	}
	
	Component makeApplicationTypePanel() {
		Box box = Box.createHorizontalBox();
		box.setBorder(new TitledBorder("アプリケーションタイプ"));
		ButtonGroup bg = new ButtonGroup();
		JRadioButton rb1 = new JRadioButton("シンプル");
		bg.add(rb1);
		box.add(rb1);
		rb1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				param.setAppType(ProjectGeneratorParameter.ApplicationType.SIMPLE);
				paramObservable.setChanged();
			}
		});
		JRadioButton rb2 = new JRadioButton("Webアプリケーション（TomcatPlugIn使用前提）");
		bg.add(rb2);
		box.add(rb2);
		rb2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
				paramObservable.setChanged();
		}
		});
		return box;
	}

	Component makeEncodingPanel() {
		Box box = Box.createHorizontalBox();
		box.setBorder(new TitledBorder("文字コード"));
		final JTextField field = new JTextField();
		field.setColumns(32);
		field.setText(param.getSourceEncoding());
		field.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				setModel(e);
			}
			public void insertUpdate(DocumentEvent e) {
				setModel(e);
			}
			public void removeUpdate(DocumentEvent e) {
				setModel(e);
			}
			private void setModel(DocumentEvent e) {
				try {
					param.setSourceEncoding(e.getDocument().getText(0, e.getDocument().getLength()));
					paramObservable.setChanged();
				} catch (BadLocationException e1) {
					throw new RuntimeException(e1);
				}
			}
		});
		box.add(field);
		return box;
	}

	Component makeProjectRootPanel() {
		Box box = Box.createHorizontalBox();
		box.setBorder(new TitledBorder("プロジェクトルートディレクトリ（Frameworkの親ディレクトリからの相対パスを推奨）"));
		final JTextField field = new JTextField();
		field.setColumns(32);
		field.setText(param.getProjectRoot());
		field.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				setModel(e);
			}
			public void insertUpdate(DocumentEvent e) {
				setModel(e);
			}
			public void removeUpdate(DocumentEvent e) {
				setModel(e);
			}
			private void setModel(DocumentEvent e) {
				try {
					param.setProjectRoot(e.getDocument().getText(0, e.getDocument().getLength()));
				} catch (BadLocationException e1) {
					throw new RuntimeException(e1);
				}
			}
		});
		box.add(field);
		JButton referenceButton = new JButton("参照...");
		box.add(referenceButton);
		referenceButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				selectProjectRoot();
			}
		});
		JButton defaultButton = new JButton("デフォルト");
		defaultButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				field.setText(param.getProjectName());
			}
		});
		box.add(defaultButton);
		return box;
	}

	Component makeProjectNamePanel() {
		Box box = Box.createHorizontalBox();
		box.setBorder(new TitledBorder("プロジェクト名"));
		final JTextField field = new JTextField();
		field.setColumns(32);
		field.setText(param.getProjectName());
		field.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				setModel(e);
			}
			public void insertUpdate(DocumentEvent e) {
				setModel(e);
			}
			public void removeUpdate(DocumentEvent e) {
				setModel(e);
			}
			private void setModel(DocumentEvent e) {
				try {
					param.setProjectName(e.getDocument().getText(0, e.getDocument().getLength()));
					paramObservable.setChanged();
				} catch (BadLocationException e1) {
					throw new RuntimeException(e1);
				}
			}
		});
		box.add(field);
		return box;
	}

	Component makeFrameworkRootPanel() {
		Box box = Box.createHorizontalBox();
		box.setBorder(new TitledBorder("RDF Core ルートディレクトリ"));
		final JTextField field = new JTextField();
		field.setColumns(32);
		field.setText(param.getFrameworkRoot());
		field.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				setModel(e);
			}
			public void insertUpdate(DocumentEvent e) {
				setModel(e);
			}
			public void removeUpdate(DocumentEvent e) {
				setModel(e);
			}
			private void setModel(DocumentEvent e) {
				try {
					param.setFrameworkRoot(e.getDocument().getText(0, e.getDocument().getLength()));
				} catch (BadLocationException e1) {
					throw new RuntimeException(e1);
				}
			}
		});
		paramObservable.addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				if(arg instanceof ProjectGeneratorParameter) {
					field.setText(((ProjectGeneratorParameter)arg).getFrameworkRoot());
				}
			}
		});
		box.add(field);
		JButton referenceButton = new JButton("参照...");
		box.add(referenceButton);
		referenceButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				selectFrameworkRoot();
			}
		});
		JButton defaultButton = new JButton("デフォルト");
		defaultButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				field.setText(ProjectGeneratorParameter.getFrameworkRootDefault());
			}
		});
		box.add(defaultButton);
		return box;
	}
	
	private void makeProject() {
		if(StringUtils.isBlank(param.getProjectName())) {
			JOptionPane.showMessageDialog(this.frame, "プロジェクト名が入力されていません。", "エラー", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if(StringUtils.isBlank(param.getProjectRoot())) {
			JOptionPane.showMessageDialog(this.frame, "プロジェクトルートディレクトリが入力されていません。", "エラー", JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
			Charset.forName(param.getSourceEncoding());
		} catch(Exception e) {
			JOptionPane.showMessageDialog(this.frame, "指定された文字コードはサポートされていません。", "エラー", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if(param.getAppType() == null) {
			JOptionPane.showMessageDialog(this.frame, "アプリケーションタイプを指定してください。", "エラー", JOptionPane.WARNING_MESSAGE);
			return;
		}
		int ret;
		if(param.getProjectRootPath().exists()) {
			ret = JOptionPane.showConfirmDialog(this.frame, "指定されたプロジェクトルートディレクトリは存在します。\n継続してよろしいですか？", "確認", 
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		} else {
			ret = JOptionPane.showConfirmDialog(this.frame, "プロジェクトを作成します。\nよろしいですか？", "確認", 
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		}
		if(ret == JOptionPane.YES_OPTION) {
			try {
				ProjectBuilder.generate(param);
				JOptionPane.showMessageDialog(this.frame, "プロジェクトの作成に成功しました。", "情報", JOptionPane.INFORMATION_MESSAGE);
				finish();
			} catch(Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this.frame, "プロジェクトの作成に失敗しました。", "エラー", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	void selectProjectRoot() {
		JFileChooser fileChooser = getFileChooser();
		fileChooser.setDialogTitle("プロジェクトルートディレクトリ");
		fileChooser.setSelectedFile(new File(param.getFrameworkRoot()));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		int ret = fileChooser.showDialog(frame, "選択");
		if(ret == JFileChooser.APPROVE_OPTION) {
			param.setProjectRoot(fileChooser.getSelectedFile().getAbsolutePath());
			paramObservable.setChanged();
		}
	}

	void selectFrameworkRoot() {
		JFileChooser fileChooser = getFileChooser();
		fileChooser.setDialogTitle("RDF Core ルートディレクトリ");
		fileChooser.setSelectedFile(new File(param.getFrameworkRoot()));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		int ret = fileChooser.showDialog(frame, "選択");
		if(ret == JFileChooser.APPROVE_OPTION) {
			param.setFrameworkRoot(fileChooser.getSelectedFile().getAbsolutePath());
			paramObservable.setChanged();
		}
	}
	
	private JFileChooser fileChooser;
	synchronized JFileChooser getFileChooser() {
		if(fileChooser == null) {
			fileChooser = new JFileChooser();
		}
		return fileChooser;
	}
	
	private void finish() {
		frame.setVisible(false);
		frame.dispose();
	}

	private ParamObserver paramObservable = new ParamObserver(); 
	class ParamObserver extends Observable {
		@Override
		public void setChanged() {
			super.setChanged();
			paramObservable.notifyObservers(param);
		}
	}
}
