using System;
using Godot;

namespace BoudysTimer;


public class BoudyTimer
{
    MainScreen mainScreen;
    PanelContainer logPanel;
	Label counter, workTally, breakTally;
	DateTime latestSessionStart;
	SessionState state;
	long workTime = 0, breakTime = 0;
	
	long BreakTime{
		set{
			breakTime = value;
		}
		get{return breakTime;}	
	}
	
	long WorkTime{
		set{
			workTime = value;
		}
		get{return workTime;}	
	}

	enum SessionState {BREAK, WORK};


	public BoudyTimer(MainScreen mainScreen, PanelContainer logPanel, Label counter, Label workTally, Label breakTally)
    {
        this.mainScreen = mainScreen;
        this.logPanel = logPanel;
        this.counter = counter;
        this.workTally = workTally;
        this.breakTally = breakTally;
		latestSessionStart = DateTime.Now;
	}

	public void Count()
	{
		var timeDifference = DateTime.Now - latestSessionStart;
		counter.Text = string.Format("{0}h {1}m {2}s", timeDifference.Hours, timeDifference.Minutes, timeDifference.Seconds);
		string str1 = state == SessionState.BREAK ? "Break" : "Work";
		string str2;
		if (timeDifference.Hours > 0)
			str2 = string.Format("{0}h {1}m", timeDifference.Hours, timeDifference.Minutes);
		else
			str2 = string.Format("{0}m", timeDifference.Minutes);
		
		mainScreen.GetWindow().Title = string.Format("{0} {1}", str1, str2);
		DrawTallies();
	}

	void DrawTallies()
	{
		var difference = DateTime.Now - latestSessionStart;
		long currentWork = 0, currentBreak = 0;
		if (state == SessionState.BREAK)
		{
			currentBreak = difference.Seconds + difference.Minutes * 60 + difference.Hours * 3600;
		}
		else
		{
			currentWork = difference.Seconds + difference.Minutes * 60 + difference.Hours * 3600;
		}
		workTally.Text = string.Format("{0}h {1}m", (WorkTime+currentWork) / 3600, (WorkTime+currentWork) / 60 % 60);
		breakTally.Text = string.Format("{0}h {1}m", (BreakTime+currentBreak) / 3600, (BreakTime+currentBreak) / 60 % 60);
	}


	public void StartBreak()
	{
		UpdateTallies();
		SetColor(SessionState.BREAK);
		state = SessionState.BREAK;
		latestSessionStart = DateTime.Now;
		string logMsg = string.Format("Break at {0}", TimeOnly.FromDateTime(latestSessionStart).ToString());
		logPanel.GetNode<Godot.Container>("ScrollContainer/VBoxContainer").AddChild(new Label() {Text = logMsg});
	}

	public void StartWork()
	{
		UpdateTallies();
		SetColor(SessionState.WORK);
		state = SessionState.WORK;
		latestSessionStart = DateTime.Now;
		string logMsg = string.Format("Work at {0}", TimeOnly.FromDateTime(latestSessionStart).ToString());
		logPanel.GetNode<Godot.Container>("ScrollContainer/VBoxContainer").AddChild(new Label() {Text = logMsg});
	}

	public void ResetAll()
	{
		BreakTime = 0; 
		WorkTime = 0;
		StartBreak();
	}

	public void ResetWork() 
	{
		WorkTime = 0;
		latestSessionStart = DateTime.Now;
		StartBreak();
	}

	void UpdateTallies()
	{
		var difference = DateTime.Now - latestSessionStart;
		if (state == SessionState.BREAK)
			BreakTime += difference.Seconds + difference.Minutes * 60 + difference.Hours * 3600;
		else
			WorkTime += difference.Seconds + difference.Minutes * 60 + difference.Hours * 3600;
	}

	void SetColor(SessionState currentState)
	{
		Color color;
		if (currentState == SessionState.BREAK)
			color = mainScreen.GetNode<Button>("Buttons/Break").SelfModulate;
		else
			color = mainScreen.GetNode<Button>("Buttons/Work").SelfModulate;
		
        // SelfModulate = color;
		logPanel.SelfModulate = color;
	}
}