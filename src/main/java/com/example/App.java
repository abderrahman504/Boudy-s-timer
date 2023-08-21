package com.example;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class App 
{
	static LocalTime start;
	public static JFrame frm; 
	public static String mode = "";
	public App()
	{
		//Set up the app window
		frm = new JFrame("Boudie's timer");
		frm.setSize(270, 200);
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.add(new Screen());
		frm.setVisible(true);
		//Set the start time
		setStartTime();
		//Create the thread that updates the title
		new TimeUpdaterThread();
	}

    public static void main(String[] args) 
	{
		//Start the App
		SwingUtilities.invokeLater(new AppThread());
    }

	public static void setStartTime() {start = LocalTime.now();}

	public static LocalTime getStartTime() {return start;}
}



class AppThread implements Runnable
{
	//Create the App window
	public void run(){new App();}
}



class TimeUpdaterThread implements Runnable
{
	public TimeUpdaterThread()
	{
		Thread t = new Thread(this, "TimeUpdater");
		t.start();
	}

	public void run()
	{
		while(true) 
		{
			try{Thread.sleep(1000);}
			catch (InterruptedException e) {System.out.println("Updater thread couldn't sleep");}
			long secondsPassed = App.getStartTime().until(LocalTime.now(), ChronoUnit.SECONDS);
			long minutesPassed = secondsPassed / 60;
			long hoursPassed = minutesPassed / 60;
			secondsPassed %= 60;
			minutesPassed %= 60;
			App.frm.setTitle(String.format("%4$s %1$dh %2$dm %3$ds", hoursPassed, minutesPassed, secondsPassed, App.mode));
		}
	}
}