import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;

class Boggle
{

	static String words[] = new String[100];
	static int x = 0;
	static String phrase = "";

	public static String AcceptPhrase()
	{
		JFrame f = new JFrame();
		String phrase1 = JOptionPane.showInputDialog(f,"Enter a phrase:");
		
		for(int i=0;i<phrase1.length();i++)
		{
			if(phrase1.charAt(i)==' ')
				phrase1 = phrase1.substring(0,i)+phrase1.substring(i+1);
		}
		if(phrase.length() < 25)
		{
			Random r = new Random();
			for(int i=phrase1.length();i<25;i++)
				phrase1 += "" + (char)(r.nextInt(25)+97);
		}
		return phrase1;
	}

	public static void main(String args[]) throws FileNotFoundException
	{
		Scanner sc = new Scanner(System.in);
		phrase = AcceptPhrase();
		
		Font  font  = new Font(Font.DIALOG_INPUT,  Font.BOLD, 25);
		
		JFrame f = new JFrame("SuperBox!");
		f.setLocation(300,200);
		f.setLayout(new GridLayout(1,2,5,5));
		
		JPanel input = new JPanel();
		JLabel heading = new JLabel("<html><h4>Entered words</h4></html>");
		JLabel enteredWords = new JLabel("<html></html>");
		enteredWords.setVerticalAlignment(JLabel.TOP);
		enteredWords.setVerticalTextPosition(JLabel.TOP);
		enteredWords.setBorder(new EmptyBorder(10,10,10,10));
		enteredWords.setBackground(Color.white);
		enteredWords.setOpaque(true);
		enteredWords.setPreferredSize(new Dimension(370,325));

		JPanel inputBar = new JPanel();
		JTextField tf = new JTextField();
		tf.setPreferredSize(new Dimension(290,30));
		JButton enter = new JButton("Enter");
		Action action = new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(!(tf.getText().equalsIgnoreCase("") || tf.getText()==null))
				{
					String entr = enteredWords.getText().substring(0,enteredWords.getText().length()-7);
					entr += "<br>"+tf.getText() +"</html>";
					enteredWords.setText(entr);
					words[x++] = tf.getText();
				}
				tf.setText("");
			}
		};
		tf.addActionListener(action);
		enter.addActionListener(action);
		JScrollPane jp = new JScrollPane(enteredWords);
		inputBar.add(tf);
		inputBar.add(enter);

		input.add(heading);
		input.add(jp);
		input.add(inputBar);

		JButton complete = new JButton("Complete");
		complete.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JFrame result = new JFrame("Results");
				
				int i=0;
				for(i=0;words[i]!=null && i<words.length;i++)
					continue;

				String data[][] = new String[i][3];
				String columns[] = {"Word","Evaluation","Score"};
				int total = 0;
				for(int j=0;j<i;j++)
				{
					data[j][0] = words[j];
					try{
						if(checkPhrase(phrase, words[j]))
						{
							if(search(words[j]))
							{
								data[j][1] = "Correct";
								int temp = getScore(words[j]);
								data[j][2] = Integer.toString(temp);
								total += temp;
							}
							else
							{
								data[j][1] = "Not a GRE word";
								data[j][2] = "NA";
							}
						}
						else
						{
							data[j][1] = "Word not getting made";
							data[j][2] = "NA";
						}
					} catch(FileNotFoundException ee)
					{JOptionPane.showMessageDialog(new JFrame("Error"),"Oops! Something went wrong!");}
				}
				JTable results = new JTable(data, columns);
				results.setBounds(50, 40, 150, 250); 
				JScrollPane jsp = new JScrollPane(results);
				
				JPanel jp1 = new JPanel();
				jp1.add(jsp);
				JLabel tot = new JLabel("Total = "+total);
				jp1.add(tot);
				JButton missed = new JButton("View missed words");
				missed.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e)
					{
						try{
							String w[] = missedWords(phrase, words);
							JFrame ff = new JFrame("Missed words");
							JPanel p1 = new JPanel();
							JList list = new JList(w);
							JScrollPane jsp1 = new JScrollPane(list);
							jsp1.setPreferredSize(new Dimension(400,400));
							p1.add(jsp1);
							ff.add(p1);
							ff.setVisible(true);
							ff.setSize(500,500);
							ff.setResizable(false);
						} catch(FileNotFoundException ee)
						{JOptionPane.showMessageDialog(new JFrame("Error"),"Oops! Something went wrong!");}
					}
				});
				jp1.add(missed);
				
				result.add(jp1);
				result.setVisible(true);
				result.setSize(500,500);
				result.setResizable(false);
			}
		});
		input.add(complete);

		JPanel grid = new JPanel();
		grid.setLayout(new GridLayout(5,5));
		
		for(int i=0;i<phrase.length();i++)
		{
			JButton b = new JButton(""+phrase.charAt(i));
			b.setFont(font);
			grid.add(b);
		}

		/*Timer timer = new Timer(500,new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				count++;
				if(count < 10000)
					label.setText(Integer.toString(count));
				else
					((Timer) (e.getSource())).stop();
			}
		});
		timer.start();*/

		f.add(input);
		f.add(grid);
		f.setSize(900,525);
		f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

	public static boolean search(String word) throws FileNotFoundException
	{
		Scanner s = new Scanner(new File("words.txt"));
		while(s.hasNextLine())
		{
			String ss = s.nextLine();
			if(ss.equalsIgnoreCase(word))
				return true;
		}
		return false;
	}

	public static boolean checkPhrase(String phrase, String word)
	{
		for(int i=0;i<word.length();i++)
		{
			if(phrase.indexOf(word.charAt(i)) == -1)
				return false;
		}
		return true;
	}

	public static int getScore(String word)
	{
		if(word.length() < 4)
			return -1;
		else 
			return word.length() - 3;
	}

	public static String[] missedWords(String phrase, String words[]) throws FileNotFoundException
	{
		Scanner s = new Scanner(new File("words.txt"));
		String missed[] = new String[2000];
		int x = 0;
		while(s.hasNextLine())
		{
			String ss = s.nextLine();
			if(checkPhrase(phrase,ss) && isMissedWord(words,ss))
				missed[x++] = ss;
		}
		int i=0;
		for(i=0;missed[i]!=null && !(missed[i].equalsIgnoreCase("")) && i<missed.length;i++)
			continue;
		String finalmissed[] = new String[i];
		for(int j=0;j<i;j++)
			finalmissed[j] = missed[j];
		return finalmissed;
	}

	public static boolean isMissedWord(String words[], String word)
	{
		for(int i=0;i<words.length &&  words[i]!=null && words[i]!="";i++)
			if(words[i].equalsIgnoreCase(word))
				return false;
		return true;
	}
}

/*

Add hint
Timer
ScrollPane
Highlight text

*/