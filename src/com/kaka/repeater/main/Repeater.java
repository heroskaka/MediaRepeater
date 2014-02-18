package com.kaka.repeater.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;

public class Repeater extends JFrame implements ActionListener, MouseMotionListener {

	String defaultFilePath = "D:\\software\\MyEclipse 6.0\\workspace\\MediaRepeater\\tmp";
	JButton startB, pauseB, selectB;
	JButton repeatA, repeatB;
	JSlider progressS;
	JLabel timeL, nameL;

	String filePath;
	String lastFilePath;
	PlayThread thread;
	
	int beginPosition,endPosition;

	public Repeater() {
		super("凯贝微型复读机");
		this.setSize(600, 200);
		this.setVisible(true);
		this.setLayout(null);

		progressS = new JSlider();
		progressS.setBounds(10, 10, 300, 40);
		progressS.setValue(0);
		this.add(progressS);
		progressS.addMouseMotionListener(this);

		timeL = new JLabel();
		timeL.setBounds(320, 30, 150, 20);
		this.add(timeL);

		nameL = new JLabel();
		nameL.setBounds(320, 10, 150, 20);
		this.add(nameL);

		selectB = new JButton();
		selectB.setBounds(480, 10, 100, 40);
		selectB.setText("选择文件");
		this.add(selectB);
		selectB.addActionListener(this);

		startB = new JButton();
		startB.setBounds(10, 60, 100, 40);
		startB.setText("开始");
		this.add(startB);
		startB.addActionListener(this);

		pauseB = new JButton();
		pauseB.setBounds(120, 60, 100, 40);
		pauseB.setText("暂停");
		this.add(pauseB);
		pauseB.addActionListener(this);

		repeatA = new JButton();
		repeatA.setBounds(10, 110, 100, 40);
		repeatA.setText("A");
		this.add(repeatA);
		repeatA.addActionListener(this);

		repeatB = new JButton();
		repeatB.setBounds(120, 110, 100, 40);
		repeatB.setText("B");
		this.add(repeatB);
		repeatB.addActionListener(this);
	}
	
	public void mouseDragged(MouseEvent e){
		if (e.getSource() == progressS) {
			if (thread != null && thread.getClip() != null) {
				int value = progressS.getValue();
				double temp = (double) value / 100;
				timeL.setText(thread
						.getTime((int) (temp * thread.getSeconds()))
						+ "/" + thread.getTime(thread.getSeconds()));
				thread.getClip().setFramePosition(
						(int) (temp * thread.getClip().getFrameLength()));
			}
		}
	}
	
	public void mouseMoved(MouseEvent e){
		
	}

	public void actionPerformed(ActionEvent e) {
		// 表明事件是selectB按钮发生的，进行选择文件操作，也可以增加键盘的快捷键
		if (e.getSource() == selectB) {
			JFileChooser fd = null;
			if (lastFilePath != null && !"".equals(lastFilePath)) {
				fd = new JFileChooser(lastFilePath);
			} else {
				fd = new JFileChooser(defaultFilePath);
			}
			fd.showOpenDialog(null);
			if (fd.getSelectedFile() != null) {
				if (thread != null) {
					thread.stop();
				}

				filePath = fd.getSelectedFile().getAbsolutePath();
				lastFilePath = filePath;

				thread = new PlayThread(filePath, progressS, timeL);
				timeL.setText(thread.getTime(0) + "/"
						+ thread.getTime(thread.getSeconds()));
				nameL.setText(fd.getSelectedFile().getName());
				progressS.setValue(0);
				progressS.setMaximum(100);
			}
		}
		if (e.getSource() == startB) {
			if (thread == null) {
				JOptionPane.showMessageDialog(null, "请先选择文件！");
			} else {
//				if(thread.getIsRepeat()){
//					thread.getClip().setFramePosition((int)((double)
//							endPosition/100*thread.getClip().getFrameLength()));
//					timeL.setText(thread
//							.getTime((int) ((double)endPosition/100 * thread.getSeconds()))
//							+ "/" + thread.getTime(thread.getSeconds()));
//				}
//				
//				thread.setStart(0);
//				thread.setEnd(0);
//				beginPosition = 0;
//				endPosition = 0;
				
				thread.setIsPause(false);
				thread.setIsRepeat(false);
				thread.start();
			}
		}
		if (e.getSource() == pauseB) {
			if (thread != null) {
				thread.setIsPause(true);
				thread.pause();
			}
		}
		if (e.getSource() == repeatA){
			if(thread != null){
				int value = progressS.getValue();
				beginPosition = value;
			}
		}
		if(e.getSource() == repeatB){
			if(thread != null){
				thread.setIsPause(true);
				thread.pause();
				
				int value = progressS.getValue();
				endPosition = value;
				if(endPosition > beginPosition){
					thread.setStart(beginPosition);
					thread.setEnd(endPosition);
					
					thread.setIsPause(false);
					thread.setIsRepeat(true);
					thread.start();
				}
			}
		}
	}

	/**
	 * @author kaka hu
	 * @time Feb 6, 2014 12:04:11 PM
	 * @param args
	 * @description
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Repeater recorder = new Repeater();
	}

}
