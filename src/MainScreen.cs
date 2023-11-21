using Godot;
using System;

namespace BoudysTimer;

public partial class MainScreen : Panel
{
	// PanelContainer logPanel;
	// Label counter, workTally, breakTally;
	// DateTime latestSessionStart;
	// SessionState state;
	// long workTime = 0, breakTime = 0;

	// enum SessionState {BREAK, WORK};
	BoudyTimer timer;

	public override void _Ready()
	{
		var logPanel = GetNode<PanelContainer>("Log Panel");
		var counter = GetNode<Label>("Counter");
		var workTally = GetNode<Label>("Work Tally");
		var breakTally = GetNode<Label>("Break Tally");
		timer = new BoudyTimer(this, logPanel, counter, workTally, breakTally);
		ConnectButtons();
		timer.StartBreak();
	}

	const float updateTime = 0.1f;
	float timeSinceUpdate = 0f;
	public override void _Process(double _delta)
	{
		timeSinceUpdate += (float)_delta;
		if (timeSinceUpdate > updateTime){
			timeSinceUpdate = 0f;
			timer.Count();
		}
	}

	void ConnectButtons()
	{
		GetNode<Button>("Buttons/Break").ButtonUp += timer.StartBreak;
		GetNode<Button>("Buttons/Work").ButtonUp += timer.StartWork;
		GetNode<Button>("Buttons/Reset Work").ButtonUp += timer.ResetWork;
		GetNode<Button>("Buttons/Reset All").ButtonUp += timer.ResetAll;
	}


}
