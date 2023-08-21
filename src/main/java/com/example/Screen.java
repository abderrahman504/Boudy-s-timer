package com.example;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;


public class Screen extends JPanel implements ActionListener
{
	JButton workbutton, breakButton;
	JPanel logPanel;
	Color workColor = new Color(139, 149, 208), breakColor = new Color(133, 186, 94);
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
		//Add entry to the log
		String str = String.format("Took break at %1$tr", LocalTime.now());
		JLabel entry = new JLabel(str);
		logPanel.add(entry);
		logPanel.setBackground(breakColor);
		// repaint();
		revalidate();
		App.mode = "Break";
		App.setStartTime();
	}
	
	void OnWorkPressed()
	{
		//Add entry to the log
		String str = String.format("Started work at %1$tr", LocalTime.now());
		JLabel entry = new JLabel(str);
		logPanel.add(entry);
		logPanel.setBackground(workColor);
		// repaint();
		revalidate();
		App.mode = "Work";
		App.setStartTime();
	}
}
