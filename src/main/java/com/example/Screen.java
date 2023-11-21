package com.example;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


public class Screen extends JPanel implements ActionListener
{
	JButton workbutton, breakButton, resetButton;
	JPanel logPanel, sidePanel;
	JScrollPane logScrollPane;
	JLabel totalWorkLabel, totalBreakLabel, runningTimeLabel;
	Color workColor = new Color(139, 149, 208), breakColor = new Color(133, 186, 94);
	Color sideTextColor = new Color(250, 25, 0);
	enum Mode {NONE, WORK, BREAK};
	static Mode mode = Mode.NONE;
	TimeUpdaterThread timeUpdaterThread;
	
	public Screen(JFrame frm)
	{
		setLayout(new BorderLayout());
		
		//Creating buttons
		workbutton = new JButton("Work");
		workbutton.addActionListener(this);
		workbutton.setBackground(workColor);
		breakButton = new JButton("Break");
		breakButton.addActionListener(this);
		breakButton.setBackground(breakColor);
		resetButton = new JButton("Reset");
		resetButton.addActionListener(this);
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		southPanel.add(workbutton);
		southPanel.add(breakButton);
		southPanel.add(resetButton);
		add(southPanel, BorderLayout.SOUTH);
		
		//Creating log panel
		logPanel = new JPanel(new GridLayout(0,1, 0, 3));
		logScrollPane = new JScrollPane(logPanel);
		add(logScrollPane, BorderLayout.CENTER);
		
		//Create running time label
		runningTimeLabel = new JLabel();
		runningTimeLabel.setFont(new Font("Ariel", Font.BOLD, 20));
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		add(topPanel, BorderLayout.NORTH);
		topPanel.add(runningTimeLabel);

		//Creating side panel
		sidePanel = new JPanel(new GridLayout(4, 1));
		
		JLabel workHeading = new JLabel("Total Work:");
		workHeading.setFont(new Font("Ariel", Font.BOLD, 14));
		sidePanel.add(workHeading);
		totalWorkLabel = new JLabel();
		totalWorkLabel.setFont(new Font("Ariel", Font.BOLD, 12));
		totalWorkLabel.setForeground(sideTextColor);
		sidePanel.add(totalWorkLabel);
		
		JLabel breakHeading = new JLabel("Total Break:");
		breakHeading.setFont(new Font("Ariel", Font.BOLD, 14));
		sidePanel.add(breakHeading);
		totalBreakLabel = new JLabel();
		totalBreakLabel.setFont(new Font("Ariel", Font.BOLD, 12));
		totalBreakLabel.setForeground(sideTextColor);
		sidePanel.add(totalBreakLabel);
		add(sidePanel, BorderLayout.WEST);

		//Creat time updater thread
		timeUpdaterThread = new TimeUpdaterThread(frm, totalWorkLabel, totalBreakLabel, runningTimeLabel);
	}

	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getActionCommand().equals("Break")) OnBreakPressed();
		else if(ae.getActionCommand().equals("Work")) OnWorkPressed();
		else if (ae.getActionCommand().equals("Reset")) OnResetPressed();
	}

	void OnBreakPressed()
	{
		logPanel.setBackground(breakColor);
		//Add entry to the log
		String str = String.format("Break at %1$tI:%1$tM %1$Tp", LocalDateTime.now());
		JLabel entry = new JLabel(str);
		logPanel.add(entry);
		//Store past work time in tally thread
		if (mode == Mode.WORK) timeUpdaterThread.addWorkTime(getPassedTimeLong());
		else if (mode == Mode.BREAK) timeUpdaterThread.addBreakTime(getPassedTimeLong());
		revalidate();
		scrollLogDown();
		App.mode = "Break";
		mode = Mode.BREAK;
		//Restart timer
		App.setStartTime();
	}
	
	void OnWorkPressed()
	{
		logPanel.setBackground(workColor);
		//Add entry to the log
		String str = String.format("Work at %1$tI:%1$tM %1$Tp", LocalDateTime.now());
		JLabel entry = new JLabel(str);
		logPanel.add(entry);
		//Store past break time in tally thread
		if (mode == Mode.BREAK)
			timeUpdaterThread.addBreakTime(getPassedTimeLong());
		if (mode == Mode.WORK) timeUpdaterThread.addWorkTime(getPassedTimeLong());
		revalidate();
		scrollLogDown();
		App.mode = "Work";
		mode = Mode.WORK;
		//Restart timer
		App.setStartTime();
	}

	void OnResetPressed()
	{
		timeUpdaterThread.reset();
		App.setStartTime();
		App.mode = "";
		mode = Mode.NONE;
		logPanel.setBackground(new Color(1f,1f,1f));
	}

	String getPassedTimeStr()
	{
		long secondsPassed = App.getStartTime().until(LocalDateTime.now(), ChronoUnit.SECONDS);
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
		return App.getStartTime().until(LocalDateTime.now(), ChronoUnit.SECONDS);
	}

	//We call this whenever we add a label to the log. 
	//We have a small delay before we scroll the log down because of a bug with Swing where the ScrollPane doesn't know its size got bigger immediately after adding a label to it.
	void scrollLogDown()
	{
		Thread t = new Thread(new Runnable() {
			public void run()
			{
				try {Thread.sleep(1);}
				catch (InterruptedException e) {System.out.println("Log updater thread couldn't sleep");}
				logScrollPane.getVerticalScrollBar().setValue(logScrollPane.getVerticalScrollBar().getMaximum());
			}
		}, "Update log thread");
		t.start();
	}
}
