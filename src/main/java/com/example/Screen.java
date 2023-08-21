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
	JPanel logPanel;
	Color workColor = new Color(139, 149, 208), breakColor = new Color(133, 186, 94);
	enum Mode {NONE, WORK, BREAK};
	Mode mode = Mode.NONE;
	
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
		// logPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 10));
		logPanel = new JPanel(new GridLayout(0,1, 0, 3));
		JScrollPane scrollPane = new JScrollPane(logPanel);
		add(scrollPane, BorderLayout.CENTER);
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
		String str = String.format("Took break at %1$tr", LocalTime.now());
		JLabel entry = new JLabel(str);
		logPanel.add(entry);
		if (mode == Mode.WORK)
		{
			long secondsPassed = App.getStartTime().until(LocalTime.now(), ChronoUnit.SECONDS);
			long minutesPassed = secondsPassed / 60;
			long hoursPassed = minutesPassed / 60;
			secondsPassed %= 60;
			minutesPassed %= 60;
			String str2;
			if(minutesPassed == 0) str2 = String.format("%1$ds", secondsPassed);
			else if(hoursPassed == 0) str2 = String.format("%2$dm %1$ds", secondsPassed, minutesPassed);
			else str2 = String.format("%3$dh %2$dm %1$ds", secondsPassed, minutesPassed, hoursPassed);
			str2 = "Worked for " + str2;
			entry = new JLabel(str2);
			logPanel.add(entry);
		}
		revalidate();
		App.mode = "Break";
		mode = Mode.BREAK;
		App.setStartTime();
	}
	
	void OnWorkPressed()
	{
		logPanel.setBackground(workColor);
		//Add entry to the log
		String str = String.format("Started work at %1$tr", LocalTime.now());
		JLabel entry = new JLabel(str);
		logPanel.add(entry);
		if (mode == Mode.BREAK)
		{
			long secondsPassed = App.getStartTime().until(LocalTime.now(), ChronoUnit.SECONDS);
			long minutesPassed = secondsPassed / 60;
			long hoursPassed = minutesPassed / 60;
			secondsPassed %= 60;
			minutesPassed %= 60;
			String str2;
			if(minutesPassed == 0) str2 = String.format("%1$ds", secondsPassed);
			else if(hoursPassed == 0) str2 = String.format("%2$dm %1$ds", secondsPassed, minutesPassed);
			else str2 = String.format("%3$dh %2$dm %1$ds", secondsPassed, minutesPassed, hoursPassed);
			str2 = "On break for " + str2;
			entry = new JLabel(str2);
			logPanel.add(entry);
		}
		revalidate();
		App.mode = "Work";
		mode = Mode.WORK;
		App.setStartTime();
	}
}
