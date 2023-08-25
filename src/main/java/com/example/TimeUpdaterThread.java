package com.example;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class TimeUpdaterThread implements Runnable 
{
	JLabel workLbl, breakLbl, runningTimeLbl;
	JFrame frm;
	long prevWorkTime, prevBreakTime;

	
	public TimeUpdaterThread(JFrame frm, JLabel workLbl, JLabel breakLbl, JLabel runningLbl)
	{
		//Setting references
		this.frm = frm;
		this.workLbl = workLbl;
		this.breakLbl = breakLbl;
		this.runningTimeLbl = runningLbl;

		//Starting updater thread
		Thread t = new Thread(this, "Updater Thread");
		t.start();
	}
	
	public void run()
	{
		while(true) 
		{
			try{Thread.sleep(100);}
			catch (InterruptedException e) {System.out.println("Updater thread couldn't sleep");}
			updateTitle();
			updateTallies();
		}
	}


	void updateTitle()
	{
		long secondsPassed = App.getStartTime().until(LocalTime.now(), ChronoUnit.SECONDS);
		long minutesPassed = secondsPassed / 60;
		long hoursPassed = minutesPassed / 60;
		minutesPassed %= 60;
		String str;
		if(hoursPassed == 0) str = String.format("%1$s %2$dm ", App.mode, minutesPassed);
		else str = String.format("%1$s %2$dh %3$dm", App.mode, hoursPassed, minutesPassed);
		frm.setTitle(str);
	}

	void updateTallies()
	{
		long runningTime = App.getStartTime().until(LocalTime.now(), ChronoUnit.SECONDS);
		//Update running tally
		long x = (runningTime / 60) % 60, y = runningTime / 3600;
		runningTimeLbl.setText(String.format("%1$dh %2$dm %3$ds", y, x, runningTime % 60));
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
			breakLbl.setText(String.format("%1$dh %2$dm", hoursPassed, minutesPassed));
			workLbl.setText(String.format("%1$dh %2$dm", prevWorkTime/3600, (prevWorkTime/60)%60));
		}
		else
		{
			workLbl.setText(String.format("%1$dh %2$dm", hoursPassed, minutesPassed));
			breakLbl.setText(String.format("%1$dh %2$dm", prevBreakTime/3600, (prevBreakTime/60)%60));	
		}
	}

	
	public void addWorkTime(long seconds) {prevWorkTime += seconds;}
	public void addBreakTime(long seconds) {prevBreakTime += seconds;}

}
