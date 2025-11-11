package edu.univ.erp.ui;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.util.AppState;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class AdminPanel extends JPanel {
    private AdminService adminService;
    private JTextField usernameField, userDetailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JLabel userDetailLabel;
    private JTextField courseCodeField, courseTitleField, courseCreditsField;
    private JComboBox<Map<String, Object>> courseComboBox;
    private JComboBox<Map<String, Object>> instructorComboBox;
    private JTextField sectionTimeField, sectionRoomField, sectionCapacityField, sectionSemesterField;
    private JCheckBox maintenanceModeBox;

    public AdminPanel(int adminId) {
        this.adminService = new AdminService();
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("User Management", createUserManagementPanel());
        tabbedPane.addTab("Course Management", createCourseManagementPanel());
        tabbedPane.addTab("Settings", createSettingsPanel());
        add(tabbedPane, BorderLayout.CENTER);
        loadDropdownData();
    }

    private JPanel createUserManagementPanel() { /* This method is likely correct, but replace to be safe */
        JPanel p = new JPanel(new GridLayout(5, 2, 10, 10)); p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        usernameField=new JTextField(); passwordField=new JPasswordField(); roleComboBox=new JComboBox<>(new String[]{"student","instructor"});
        userDetailLabel=new JLabel("Roll No:"); userDetailField=new JTextField();
        p.add(new JLabel("Username:")); p.add(usernameField); p.add(new JLabel("Password:")); p.add(passwordField);
        p.add(new JLabel("Role:")); p.add(roleComboBox); p.add(userDetailLabel); p.add(userDetailField);
        JButton b=new JButton("Add User"); p.add(new JLabel()); p.add(b);
        roleComboBox.addActionListener(e -> userDetailLabel.setText("student".equals(roleComboBox.getSelectedItem())?"Roll No:":"Department:"));
        b.addActionListener(e -> onAddUserClick()); return p;
    }
    private JPanel createCourseManagementPanel() { /* This method is likely correct, but replace to be safe */
        JPanel p=new JPanel(new GridBagLayout()); GridBagConstraints c=new GridBagConstraints(); c.insets=new Insets(5,5,5,5); c.fill=GridBagConstraints.HORIZONTAL;
        c.gridx=0;c.gridy=0;c.gridwidth=2;p.add(new JLabel("--- Create New Course ---"),c);c.gridwidth=1;
        c.gridx=0;c.gridy=1;p.add(new JLabel("Course Code:"),c);c.gridx=1;c.gridy=1;courseCodeField=new JTextField(15);p.add(courseCodeField,c);
        c.gridx=0;c.gridy=2;p.add(new JLabel("Course Title:"),c);c.gridx=1;c.gridy=2;courseTitleField=new JTextField(15);p.add(courseTitleField,c);
        c.gridx=0;c.gridy=3;p.add(new JLabel("Credits:"),c);c.gridx=1;c.gridy=3;courseCreditsField=new JTextField(15);p.add(courseCreditsField,c);
        c.gridx=1;c.gridy=4;JButton b1=new JButton("Create Course");p.add(b1,c);b1.addActionListener(e->onAddCourseClick());
        c.gridx=0;c.gridy=5;c.gridwidth=2;p.add(new JSeparator(),c);c.gridy=6;p.add(new JLabel("--- Create New Section ---"),c);c.gridwidth=1;
        c.gridx=0;c.gridy=7;p.add(new JLabel("Select Course:"),c);c.gridx=1;c.gridy=7;courseComboBox=new JComboBox<>();p.add(courseComboBox,c);
        c.gridx=0;c.gridy=8;p.add(new JLabel("Assign Instructor:"),c);c.gridx=1;c.gridy=8;instructorComboBox=new JComboBox<>();p.add(instructorComboBox,c);
        c.gridx=0;c.gridy=9;p.add(new JLabel("Time:"),c);c.gridx=1;c.gridy=9;sectionTimeField=new JTextField(15);p.add(sectionTimeField,c);
        c.gridx=0;c.gridy=10;p.add(new JLabel("Room:"),c);c.gridx=1;c.gridy=10;sectionRoomField=new JTextField(15);p.add(sectionRoomField,c);
        c.gridx=0;c.gridy=11;p.add(new JLabel("Capacity:"),c);c.gridx=1;c.gridy=11;sectionCapacityField=new JTextField(15);p.add(sectionCapacityField,c);
        c.gridx=0;c.gridy=12;p.add(new JLabel("Semester:"),c);c.gridx=1;c.gridy=12;sectionSemesterField=new JTextField("Fall",15);p.add(sectionSemesterField,c);
        c.gridx=1;c.gridy=13;JButton b2=new JButton("Create Section");p.add(b2,c);b2.addActionListener(e->onAddSectionClick());
        courseComboBox.setRenderer(new DefaultListCellRenderer(){public Component getListCellRendererComponent(JList<?> l,Object v,int i,boolean s,boolean f){if(v instanceof Map)v=((Map<?,?>)v).get("displayText");return super.getListCellRendererComponent(l,v,i,s,f);}});
        instructorComboBox.setRenderer(new DefaultListCellRenderer(){public Component getListCellRendererComponent(JList<?> l,Object v,int i,boolean s,boolean f){if(v instanceof Map)v=((Map<?,?>)v).get("displayText");return super.getListCellRendererComponent(l,v,i,s,f);}});
        return p;
    }
    private JPanel createSettingsPanel() {
        JPanel p=new JPanel(new FlowLayout(FlowLayout.LEFT)); p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        maintenanceModeBox=new JCheckBox("Enable Maintenance Mode"); maintenanceModeBox.setSelected(AppState.isMaintenanceMode);
        maintenanceModeBox.addActionListener(e->{boolean s=maintenanceModeBox.isSelected();AppState.isMaintenanceMode=s;adminService.setMaintenanceMode(s);JOptionPane.showMessageDialog(this,"Maintenance Mode has been turned "+(s?"ON":"OFF")+".");});
        p.add(maintenanceModeBox); return p;
    }
    private void loadDropdownData(){/*...unchanged...*/List<Map<String,Object>>cs=adminService.getAllCourses();courseComboBox.removeAllItems();for(Map<String,Object>c:cs)courseComboBox.addItem(c);List<Map<String,Object>>is=adminService.getAllInstructors();instructorComboBox.removeAllItems();for(Map<String,Object>i:is)instructorComboBox.addItem(i);}
    private void onAddUserClick(){/*...unchanged...*/String u=usernameField.getText(),p=new String(passwordField.getPassword()),r=(String)roleComboBox.getSelectedItem(),d=userDetailField.getText();if(u.isEmpty()||p.isEmpty()||d.isEmpty()){JOptionPane.showMessageDialog(this,"Please fill all user fields.","Input Error",JOptionPane.ERROR_MESSAGE);return;}String res=adminService.addUser(u,p,r,d);JOptionPane.showMessageDialog(this,res);if(res.contains("successfully")){usernameField.setText("");passwordField.setText("");userDetailField.setText("");}}
    private void onAddCourseClick(){/*...unchanged...*/String c=courseCodeField.getText(),t=courseTitleField.getText(),cred=courseCreditsField.getText();if(c.isEmpty()||t.isEmpty()||cred.isEmpty()){JOptionPane.showMessageDialog(this,"Please fill all course fields.","Input Error",JOptionPane.ERROR_MESSAGE);return;}try{int cr=Integer.parseInt(cred);String res=adminService.createCourse(c,t,cr);JOptionPane.showMessageDialog(this,res);if(res.contains("successfully")){courseCodeField.setText("");courseTitleField.setText("");courseCreditsField.setText("");loadDropdownData();}}catch(NumberFormatException e){JOptionPane.showMessageDialog(this,"Credits must be a number.","Input Error",JOptionPane.ERROR_MESSAGE);}}
    private void onAddSectionClick(){/*...unchanged...*/Map<String,Object>sc=(Map<String,Object>)courseComboBox.getSelectedItem(),si=(Map<String,Object>)instructorComboBox.getSelectedItem();String t=sectionTimeField.getText(),r=sectionRoomField.getText(),cap=sectionCapacityField.getText(),sem=sectionSemesterField.getText();if(sc==null||si==null||t.isEmpty()||r.isEmpty()||cap.isEmpty()){JOptionPane.showMessageDialog(this,"Please fill all section fields.","Input Error",JOptionPane.ERROR_MESSAGE);return;}try{int cid=(int)sc.get("id"),iid=(int)si.get("id"),capacity=Integer.parseInt(cap);if(capacity<=0){JOptionPane.showMessageDialog(this,"Capacity must be a positive number.","Input Error",JOptionPane.ERROR_MESSAGE);return;}String res=adminService.createSection(cid,iid,t,r,capacity,sem);JOptionPane.showMessageDialog(this,res);if(res.contains("successfully")){sectionTimeField.setText("");sectionRoomField.setText("");sectionCapacityField.setText("");}}catch(NumberFormatException e){JOptionPane.showMessageDialog(this,"Capacity must be a number.","Input Error",JOptionPane.ERROR_MESSAGE);}}
}