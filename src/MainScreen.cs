using Godot;
using System;

namespace BoudysTimer;

public partial class MainScreen : Panel
{
	PanelContainer logPanel;
	Label counter, workTally, breakTally;
	DateTime latestSessionStart;
	SessionState state;
	long workTime, breakTime;

	enum SessionState {BREAK, WORK};


	public override void _Ready()
	{
		logPanel = GetNode<PanelContainer>("Log Panel");
		counter = GetNode<Label>("Counter");
		workTally = GetNode<Label>("Work Tally");
		breakTally = GetNode<Label>("Break Tally");
		ConnectButtons();
		StartBreak();
	}

	const float updateTime = 0.1f;
	float timeSinceUpdate = 0f;
	public override void _Process(double _delta)
	{
		timeSinceUpdate += (float)_delta;
		if (timeSinceUpdate > updateTime){
			timeSinceUpdate = 0f;
			Update();
		}
	}

	void ConnectButtons()
	{
		GetNode<Button>("Buttons/Break").ButtonUp += StartBreak;
		GetNode<Button>("Buttons/Work").ButtonUp += StartWork;
		GetNode<Button>("Buttons/Reset Work").ButtonUp += ResetWork;
		GetNode<Button>("Buttons/Reset All").ButtonUp += ResetAll;
	}

	void Update()
	{
		var timeDifference = DateTime.Now.Subtract(latestSessionStart);
		counter.Text = string.Format("{0}h {1}m {2}s", timeDifference.Hours, timeDifference.Minutes, timeDifference.Seconds);
		string str1 = state == SessionState.BREAK ? "Break" : "Work";
		string str2;
		if (timeDifference.Hours > 0)
			str2 = string.Format("{0}h {1}m", timeDifference.Hours, timeDifference.Minutes);
		else
			str2 = string.Format("{0}m", timeDifference.Minutes);
		
		GetWindow().Title = string.Format("{0} {1}", str1, str2);
	}


	void StartBreak()
	{
		// UpdateTallies();
		SetColor(SessionState.BREAK);
		state = SessionState.BREAK;
		latestSessionStart = DateTime.Now;
		var cont = logPanel.GetNode<Godot.Container>("ScrollContainer/VBoxContainer");
		cont.AddChild(new Label() {Text = "Break started at " + latestSessionStart.ToString()});
	}

	void StartWork()
	{
		// UpdateTallies();
		SetColor(SessionState.WORK);
		state = SessionState.WORK;
		latestSessionStart = DateTime.Now;
		logPanel.GetNode<Godot.Container>("ScrollContainer/VBoxContainer").AddChild(new Label() {Text = "Work started at " + latestSessionStart.ToString()});
	}

	void ResetAll()
	{
		throw new NotImplementedException();

	}

	void ResetWork()
	{
		throw new NotImplementedException();
	}

	void UpdateTallies()
	{
		throw new NotImplementedException();
	}

	void SetColor(SessionState currentState)
	{
		Color color;
		if (currentState == SessionState.BREAK)
			color = GetNode<Button>("Buttons/Break").SelfModulate;
		else
			color = GetNode<Button>("Buttons/Work").SelfModulate;
		// SelfModulate = color;
		logPanel.SelfModulate = color;
	}
}
