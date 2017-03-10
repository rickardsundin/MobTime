package com.greatwebguy.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Settings {
	private static Settings settings = new Settings();
	private int DEFAULT_START = 7;
	private int startTime = DEFAULT_START;
	private LocalTime time;
	private int currentUser = 0;
	private int nextUser = 1;
	
	protected ObservableList<People> users = FXCollections.observableArrayList();
	protected StringProperty userMessage = new SimpleStringProperty("");
	protected StringProperty userName = new SimpleStringProperty("MobTime");
    protected StringProperty nextUserName = new SimpleStringProperty("");

	private Settings() {
		//
	}
	
	public static Settings instance() {
		return settings;
	}
	
	public void initializeTime() {
		time = LocalTime.of(0, startTime, 0);
	}
	
	public void setStartTime(int minutes) {
		startTime = minutes;
	}
	
	public int getStartTime() {
		return startTime;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}
	
	public int getTimeInSeconds() {
		return new SimpleIntegerProperty(startTime * 60).get() + 1;
	}
	
	public void setCurrentUser(int index) {
		currentUser = index;
		displayUserMessage();
	}
	
	public int getCurrentUser() {
		return users.size() > 0 ? currentUser:-1;
	}

	public void incrementCurrentUser() {
		int potentialUser = getCurrentUser()+1;
		if(potentialUser == 0) {
			displayUserMessage();
		} else {
			currentUser = potentialUser >= users.size()?0:potentialUser;
			displayUserMessage();
		}
	}
	
    public void displayUserMessage() {
    	int index = getCurrentUser();
    	if(index > -1) {
    		String name = users.get(index).getName();
    		userMessage.set(name +"'s Turn");
    		userName.set(name);
    		nextUserName.set(">>" + users.get(nextUserIndex(index)).getName());
    	} else {
    		userMessage.set("");
    		userName.set("MobTime");
            nextUserName.set("");
        }
	}

    private int nextUserIndex(int index) {
        if (index > -1) {
            return (index + 1) % users.size();
        }
        return index;
    }

	public void storeUsers() {
		String path = getUserStoragePath();
		try (PrintWriter out = new PrintWriter(path)) {
			out.println(Settings.instance().users.stream().map(People::getName).collect(Collectors.joining(",")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void loadUsers() {
		String path = getUserStoragePath();
		try (BufferedReader brTest = new BufferedReader(new FileReader(path))) {
			String users = brTest.readLine();
			String[] people = users.split(",");
			for (String person : people) {
				if (StringUtils.isNotBlank(person)) {
					Settings.instance().users.add(new People(person));
				}
			}
		} catch (Exception e) {
			System.out.println("No existing users found");
		}
	}
	
	public void storeTime() {
		String path = getTimeStoragePath();
		try (PrintWriter out = new PrintWriter(path)) {
			out.println(Settings.instance().getStartTime());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}
	
	public void loadTime() {
		String path = getTimeStoragePath();
		try (BufferedReader brTime = new BufferedReader(new FileReader(path))) {
			String time = brTime.readLine();
			if(StringUtils.isNotBlank(time)) {
				Settings.instance().setStartTime(Integer.parseInt(time));
			}
		} catch (Exception e) {
			System.out.println("No existing time");
		}		
	}

	private String getUserStoragePath() {
		String home = System.getProperty("user.home");
		String path = home + File.separator + ".mobtime";
		return path;
	}
	
	private String getTimeStoragePath() {
		String home = System.getProperty("user.home");
		String path = home + File.separator + ".mobtime-time";
		return path;
	}
	
}
