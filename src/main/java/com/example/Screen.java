package com.example;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;


public class Screen extends JPanel implements ActionListener
{
	JButton workbutton, breakButton;
	JPanel logPanel, sidePanel;
	JLabel totalWorkLabel, totalBreakLabel, runningTimeLabel;
	Color workColor = new Color(139, 149, 208), breakColor = new Color(133, 186, 94);
	Color sideTextColor = new Color(250, 25, 0);
	enum Mode {NONE, WORK, BREAK};
	static Mode mode = Mode.NONE;
	TimeTallyThread tallyThread;
	
	public Screen()
	{
		setLayout(new BorderLayout());
		
		//Creating buttons
		workbutton = new JButton("Work");
		workbutton.addActionListener(this);
		workbutton.setBackground(workColor);
		breakButton = new JButton("Break");
		breakButton.addActionListener(this);
		breakButton.setBackground(breakColor);
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		southPanel.add(workbutton);
		southPanel.add(breakButton);
		add(southPanel, BorderLayout.SOUTH);
		
		//Creating log panel
		logPanel = new JPanel(new GridLayout(0,1, 0, 3));
		JScrollPane scrollPane = new JScrollPane(logPanel);
		add(scrollPane, BorderLayout.CENTER);

		//Creating side panel
		sidePanel = new JPanel(new GridLayout(4, 1));
		sidePanel.add(new JLabel("Total Work:"));
		totalWorkLabel = new JLabel();
		totalWorkLabel.setForeground(sideTextColor);
		sidePanel.add(totalWorkLabel);
		
		sidePanel.add(new JLabel("Total Break:"));
		totalBreakLabel = new JLabel();
		totalBreakLabel.setForeground(sideTextColor);
		sidePanel.add(totalBreakLabel);
		add(sidePanel, BorderLayout.WEST);

		//Create running time label
		runningTimeLabel = new JLabel();
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		add(topPanel, BorderLayout.NORTH);
		topPanel.add(runningTimeLabel);
		//Creat time tally thread
		tallyThread = new TimeTallyThread(totalWorkLabel, totalBreakLabel, runningTimeLabel);

	}

	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getActionCommand().equals("Break")) OnBreakPressed();
		else if(ae.getActionCommand().equals("Work")) OnWorkPressed();
	}

	void OnBreakPressed()
	{
		logPanel.setBackground(breakColor);
		//Add entry to the log
		String str = String.format("Break at %1$tI:%1$tM %1$Tp", LocalTime.now());
		JLabel entry = new JLabel(str);
		logPanel.add(entry);
		if (mode == Mode.WORK)
		{
			tallyThread.addWorkTime(getPassedTimeLong());
			String str2 = getPassedTimeStr();
			str2 = "Stopped work after " + str2;
			entry = new JLabel(str2);
			logPanel.add(entry);
		}
		revalidate();
		App.mode = "Break";
		mode = Mode.BREAK;
		App.setStartTime();
	}
	//%tI:%tM %Tp
	
	void OnWorkPressed()
	{
		logPanel.setBackground(workColor);
		//Add entry to the log
		String str = String.format("Work at %1$tI:%1$tM %1$Tp", LocalTime.now());
		JLabel entry = new JLabel(str);
		logPanel.add(entry);
		if (mode == Mode.BREAK)
		{
			tallyThread.addBreakTime(getPassedTimeLong());
			String str2 = getPassedTimeStr();
			str2 = "Stopped break after " + str2;
			entry = new JLabel(str2);
			logPanel.add(entry);
		}
		revalidate();
		App.mode = "Work";
		mode = Mode.WORK;
		App.setStartTime();
	}


	String getPassedTimeStr()
	{
		long secondsPassed = App.getStartTime().until(LocalTime.now(), ChronoUnit.SECONDS);
		long minutesPassed = secondsPassed / 60;
		long hoursPassed = minutesPassed / 60;
		minutesPassed %= 60;
		String str;
		if(hoursPassed == 0) str = String.format("%1$dm", minutesPassed);
		else str = String.format("%1$dh %2$dm", hoursPassed, minutesPassed);
		return str;
	}

	long getPassedTimeLong()
	{
		return App.getStartTime().until(LocalTime.now(), ChronoUnit.SECONDS);
	}
}

class TimeTallyThread implements Runnable
{
	JLabel workTally, breakTally, runningTimeTally;
	long prevWorkTime, prevBreakTime;
	public TimeTallyThread(JLabel workLbl, JLabel breakLbl, JLabel runningTimeLbl)
	{
		workTally = workLbl;
		breakTally = breakLbl;
		runningTimeTally = runningTimeLbl;
		Thread t = new Thread(this, "Tally thread");
		t.start();
	}

	public void run()
	{
		while (true)
		{
			try {Thread.sleep(250);}
			catch (InterruptedException e) {System.out.println("Tally thread couldn't sleep");}
			
			long runningTime = App.getStartTime().until(LocalTime.now(), ChronoUnit.SECONDS);
			//Update running tally
			long x = (runningTime / 60) % 60, y = runningTime / 3600;
			runningTimeTally.setText(String.format("%1$dh %2$dm %3$ds", y, x, runningTime % 60));
			//Update work and break tallies
			long secondsPassed=0, minutesPassed, hoursPassed;
			if (Screen.mode == Screen.Mode.BREAK) secondsPassed = runningTime + prevBreakTime;
			else if (Screen.mode == Screen.Mode.WORK) secondsPassed = runningTime + prevWorkTime;
			minutesPassed = secondsPassed / 60;
			hoursPassed = minutesPassed / 60;
			minutesPassed %= 60;
			secondsPassed %= 60;
			if (Screen.mode == Screen.Mode.BREAK)
			{
				breakTally.setText(String.format("%1$dh %2$dm", hoursPassed, minutesPassed));
				workTally.setText(String.format("%1$dh %2$dm", prevWorkTime/3600, (prevWorkTime/60)%60));
			}
			else
			{
				workTally.setText(String.format("%1$dh %2$dm", hoursPassed, minutesPassed));
				breakTally.setText(String.format("%1$dh %2$dm", prevBreakTime/3600, (prevBreakTime/60)%60));	
			}
		}
	}

	public void addWorkTime(long seconds) {prevWorkTime += seconds;}
	public void addBreakTime(long seconds) {prevBreakTime += seconds;}
}
