package com.chinawayltd.mqtt.push.ui;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class MainUI {
	
	private static String[] qos= {"0","1","2"};
	private static String clientId="mqtt_push_product";
	private static String username="admin";
	private static String password="Zaqrwe2017";
	
	public static void main(String[] args) {
		startMenu();
	}
	
	public static void startMenu() {
		final JFrame frame=new JFrame();
		frame.setSize(360,400);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width-360)/2,
				(Toolkit.getDefaultToolkit().getScreenSize().height-320)/2);
		frame.setTitle("Chinawayltd MqttPush");
		frame.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel jLabel_host=new JLabel("Host：");
		JTextField jTextField_host=new JTextField();
		frame.add(jLabel_host);
		jLabel_host.setBounds(20, 20, 80, 20);
		frame.add(jTextField_host);
		jTextField_host.setBounds(120, 20, 200, 20);
		
		JLabel jLabel_port=new JLabel("Port：");
		JTextField jTextField_port=new JTextField();
		frame.add(jLabel_port);
		jLabel_port.setBounds(20, 60, 80, 20);
		frame.add(jTextField_port);
		jTextField_port.setBounds(120, 60, 200, 20);

		JLabel jLabel_topic=new JLabel("Topic：");
		JTextField jTextField_topic=new JTextField();
		frame.add(jLabel_topic);
		jLabel_topic.setBounds(20, 100, 80, 20);
		frame.add(jTextField_topic);
		jTextField_topic.setBounds(120, 100, 200, 20);

		JLabel jLabel_content=new JLabel("Content：");
		JTextField jTextField_content=new JTextField();
		frame.add(jLabel_content);
		jLabel_content.setBounds(20, 140, 80, 20);
		frame.add(jTextField_content);
		jTextField_content.setBounds(120, 140, 200, 20);

		JLabel jLabel_qos=new JLabel("Qos：");
		JComboBox<String> jComboBox_qos=new JComboBox<String>(qos);
		frame.add(jLabel_qos);
		jLabel_qos.setBounds(20, 180, 80, 20);
		frame.add(jComboBox_qos);
		jComboBox_qos.setBounds(120, 180, 180, 20);
		
		JLabel jLabel_times=new JLabel("Times：");
		JTextField jTextField_times=new JTextField();
		frame.add(jLabel_times);
		jLabel_times.setBounds(20, 220, 80, 20);
		frame.add(jTextField_times);
		jTextField_times.setBounds(120, 220, 200, 20);

		JCheckBox jCheckBox_ssl=new JCheckBox("启用SSL");
		frame.add(jCheckBox_ssl);
		jCheckBox_ssl.setBounds(20, 260, 80, 20);
		
		JButton jButton_submit=new JButton("提交");
		frame.add(jButton_submit);
		jButton_submit.setBounds(70, 300, 80, 30);
		jButton_submit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String host=jTextField_host.getText().trim();
				String port=jTextField_port.getText().trim();
				String topic=jTextField_topic.getText().trim();
				String content=jTextField_content.getText().trim();
				Integer qos=jComboBox_qos.getSelectedIndex();
				String times=jTextField_times.getText().trim();
				Boolean ssl=jCheckBox_ssl.isSelected();
				
				if(host.equals("")||host==null){
					JOptionPane.showMessageDialog(frame,"请输入Host");
					return;
				}
				if(port.equals("")||port==null){
					JOptionPane.showMessageDialog(frame,"请输入Port");
					return;
				}
				if(topic.equals("")||topic==null){
					JOptionPane.showMessageDialog(frame,"请输入Topic");
					return;
				}
				if(content.equals("")||content==null){
					JOptionPane.showMessageDialog(frame,"请输入Content");
					return;
				}
				int time=Integer.parseInt(times);
				
				String broker=host+":"+port;
				if(ssl){
					broker="ssl://"+broker;
				}else{
					broker="tcp://"+broker;
				}
				MemoryPersistence persistence=new MemoryPersistence();
				try{
		            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
		            MqttConnectOptions connOpts = new MqttConnectOptions();
		            connOpts.setUserName(username);
		            connOpts.setPassword(password.toCharArray());
		            connOpts.setCleanSession(true);
		            connOpts.setAutomaticReconnect(true);
		            sampleClient.setCallback(new MqttCallback() {
		                @Override
		                public void connectionLost(Throwable cause) {
		                    System.out.println("lost");
		                }
		                @Override
		                public void messageArrived(String topic, MqttMessage message) throws Exception {
		                    // TODO Auto-generated method stub
		                    
		                }
		                @Override
		                public void deliveryComplete(IMqttDeliveryToken token) {
		                    // TODO Auto-generated method stub
		                }
		                
		            });
		            System.out.println("Connecting to broker: "+broker);
		            sampleClient.connect(connOpts);
		            System.out.println("Connected");
		            MqttMessage message = new MqttMessage(content.getBytes());
		            message.setQos(qos);
		            message.setRetained(true);
		            for(int i=1; i<time; i++) {
		                sampleClient.publish(topic, message);
		                Thread.sleep(1 * 1000);
		                System.out.println(System.currentTimeMillis()+" : Message published");
		            }
		            System.out.println("Message published");
		            sampleClient.disconnect();
		            System.out.println("Disconnected");
		            System.exit(0);
		        } catch(MqttException me) {
		        	String reason="reason : "+me.getReasonCode()+"\n"+
		        			"msg : "+me.getMessage()+"\n"+
		        			"loc "+me.getLocalizedMessage()+"\n"+
		        			"cause "+me.getCause()+"\n"+
		        			"excep "+me;
		           JOptionPane.showMessageDialog(frame, reason);
		        } catch(InterruptedException ie) {
		        	JOptionPane.showMessageDialog(frame, ie);
		        }
			}
		});
		
		JButton jButton_reset=new JButton("重置");
		frame.add(jButton_reset);
		jButton_reset.setBounds(210, 300, 80, 30);
		jButton_reset.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jTextField_host.setText("");
				jTextField_port.setText("");
				jTextField_topic.setText("");
				jTextField_content.setText("");
				jTextField_times.setText("");
				jCheckBox_ssl.setSelected(false);
			}
		});
		
	}
	
	
	
}
