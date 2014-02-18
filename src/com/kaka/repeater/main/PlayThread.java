package com.kaka.repeater.main;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.JLabel;
import javax.swing.JSlider;

public class PlayThread implements Runnable {
	private String filePath;
	private boolean isPause = false;
	private boolean isRepeat = false;
	private int start = 0;
	private int end = 0;

	private Clip clip;

	private Thread thread;

	private JSlider progressS;
	private JLabel timeL;

	public PlayThread(String filePath, JSlider progressS, JLabel timeL) {
		this.filePath = filePath;
		this.progressS = progressS;
		this.timeL = timeL;

		init();
	}

	public void init() {
		try {
			// getAudioInputStream() also accepts a File or InputStream
			AudioInputStream ais = AudioSystem.getAudioInputStream(new File(
					filePath));
			AudioFormat audioFormat = ais.getFormat();
			// 转换mp3文件编码
			if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
				audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
						audioFormat.getSampleRate(), 16, audioFormat
								.getChannels(), audioFormat.getChannels() * 2,
						audioFormat.getSampleRate(), false);
				ais = AudioSystem.getAudioInputStream(audioFormat, ais);
			}

			// 打开输出设备
			DataLine.Info dataLineInfo = new DataLine.Info(Clip.class,
					audioFormat, AudioSystem.NOT_SPECIFIED);

			clip = (Clip) AudioSystem.getLine(dataLineInfo);
			clip.open(ais);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void play() {
		try {
			double temp;
			if(isRepeat){
				clip.setLoopPoints((int)((double)start/100*clip.getFrameLength()),
						(int)((double)end/100*clip.getFrameLength()));
				
				temp = (double) (clip.getFramePosition() % (clip.getFrameLength()
						*(end-start)/100))
						/ clip.getFrameLength();
			}else{
				clip.setLoopPoints(0, -1);
				
				temp = (double) (clip.getFramePosition() % clip.getFrameLength())
						/ clip.getFrameLength();
			}
			clip.loop(Clip.LOOP_CONTINUOUSLY);
			clip.start();
			
			progressS.setValue((int) (temp * 100));
			timeL.setText(getTime((int) (temp * getSeconds())) + "/"
				+ getTime(getSeconds()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pause() {
		try {
			clip.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public double getDuration() {
		double duration = 0.0;
		duration = clip.getBufferSize()
				/ (clip.getFormat().getFrameSize() * clip.getFormat()
						.getFrameRate());
		return duration;
	}

	public int getSeconds() {
		double seconds = 0.0;
		seconds = clip.getMicrosecondLength() / 1000000;
		return (int) Math.floor(seconds);
	}

	public Clip getClip() {
		return clip;
	}

	public void setClip(Clip clip) {
		this.clip = clip;
	}

	public String getTime(int seconds) {
		String time = "";
		int hour = seconds / 3600;
		if (hour < 10) {
			time += "0" + hour + ":";
		} else {
			time += hour + ":";
		}
		int minute = (seconds - hour * 3600) / 60;
		if (minute < 10) {
			time += "0" + minute + ":";
		} else {
			time += minute + ":";
		}
		int second = (seconds - hour * 3600 - minute * 60);
		if (second < 10) {
			time += "0" + second;
		} else {
			time += second;
		}
		return time;
	}

	public void start() {
		thread = new Thread(this);
		thread.setName("kaka");
		thread.start();
	}

	public void stop() {
		if (thread != null && clip != null) {
			clip.stop();
			clip.close();
			thread.interrupt();
		}
		clip = null;
		thread = null;
	}

	public void run() {
		do {
			play();
			// take a little break between sounds
			try {
				thread.sleep(222);
			} catch (Exception e) {
				break;
			}
		} while (!isPause);
	}

	public boolean getIsPause() {
		return isPause;
	}

	public void setIsPause(boolean isPause) {
		this.isPause = isPause;
	}
	
	public boolean getIsRepeat() {
		return isRepeat;
	}

	public void setIsRepeat(boolean isRepeat) {
		this.isRepeat = isRepeat;
	}
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

}
