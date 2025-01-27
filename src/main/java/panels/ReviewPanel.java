package panels;

import models.CurrentUser;
import models.Review;
import models.User;
import utils.ReviewFileManager;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.util.List;

public class ReviewPanel extends JPanel {
    private Review review;
    private List<User> users;
    private List<Review> reviews;
    private CurrentUser currentUser;

    private JPanel buttonPanel;
    private JPanel contentPanel;
    private JButton searchButton;
    private JTextField searchTextField;
    private JPanel searchPanel;
    private JPanel guidePanel;
    private JLabel reviewLabel;
    private JPanel reviewsPanel;

    public ReviewPanel(List<User> users, CurrentUser currentUser) throws FileNotFoundException {
        this.users = users;
        this.currentUser = currentUser;

        this.setLayout(new BorderLayout());
        this.setOpaque(false);

        ReviewFileManager reviewFileManager = new ReviewFileManager();
        reviews = reviewFileManager.loadReviews();

        buttonPanel(reviews);
    }

    public ReviewPanel(Review review, List<User> users, CurrentUser currentUser) throws FileNotFoundException {
        this.review = review;
        this.users = users;
        this.currentUser = currentUser;

        this.setLayout(new BorderLayout());
        this.setOpaque(false);

        ReviewFileManager reviewFileManager = new ReviewFileManager();
        reviews = reviewFileManager.loadReviews();

        buttonPanel(reviews);

    }

    private void buttonPanel(List<Review> reviews) {
        buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        this.add(buttonPanel, BorderLayout.PAGE_START);

        buttonPanel.add(mainButton());
        buttonPanel.add(checkListButton());
        buttonPanel.add(createReviewButton());
        buttonPanel.add(logoutButton());

        contentPanel();
    }

    private JButton mainButton() {
        JButton mainButton = new JButton("메인 화면");
        mainButton.addActionListener(event -> {
            updateContentPanel(new MainPanel(users, currentUser));
        });

        return mainButton;
    }

    private JButton checkListButton() {
        JButton checkListButton = new JButton("체크 리스트");
        checkListButton.addActionListener(event -> {
            try {
                updateContentPanel(new CheckListPanel(currentUser));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        return checkListButton;
    }

    private JButton createReviewButton() {
        JButton createReviewButton = new JButton("글쓰기");
        createReviewButton.addActionListener(event -> {
            updateContentPanel(new WriteReviewPanel(users, reviews, currentUser));
        });

        return createReviewButton;
    }

    private JButton logoutButton() {
        JButton logoutButton = new JButton("로그아웃");
        logoutButton.addActionListener(event -> {
            JOptionPane optionPane = new JOptionPane();

            JOptionPane.showMessageDialog(null, "로그아웃 되었습니다.", "Fries!", JOptionPane.PLAIN_MESSAGE);

            currentUser.logout();

            try {
                updateContentPanel(new InitLoginPanel());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        return logoutButton;
    }

    private void contentPanel() {
        contentPanel = new JPanel();
        contentPanel.setBackground(new Color(0, 0, 0, 122));
        contentPanel.setBorder(new LineBorder(Color.BLACK, 1));
        contentPanel.setLayout(new GridLayout(0, 1));
        contentPanel.setPreferredSize(new Dimension(550, 350));

        this.add(contentPanel, BorderLayout.SOUTH);

        guidePanel();
    }

    private void guidePanel() {
        guidePanel = new JPanel();
        guidePanel.setOpaque(false);
        JLabel label1 = new JLabel("제목");
        label1.setForeground(Color.WHITE);
        label1.setPreferredSize(new Dimension(140, 15));
        guidePanel.add(label1);

        JLabel label2 = new JLabel("작성자");
        label2.setForeground(Color.WHITE);
        label2.setPreferredSize(new Dimension(35, 15));
        guidePanel.add(label2);

        contentPanel.add(guidePanel, BorderLayout.PAGE_START);

        reviewsPanel();
    }

    private void reviewsPanel() {
        for (Review review : reviews) {
            if (review.status().equals("delete")) {
                continue;
            }
            reviewsPanel = new JPanel();
            reviewsPanel.setBackground(new Color(255, 255, 255, 122));
            reviewsPanel.setBorder(new LineBorder(Color.BLACK, 1));
            contentPanel.add(reviewsPanel);

            reviewLabel = new JLabel();
            reviewLabel.setHorizontalAlignment(JLabel.CENTER);
            reviewLabel.setText(review.category() + " " + review.title() + "                       "
                    + review.userId());
            reviewsPanel.add(reviewLabel);

            reviewLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    updateContentPanel(new displayReviewPanel(review, users, reviews, currentUser));
                }
            });
        }
    }

    private void updateContentPanel(JPanel panel) {
        this.removeAll();
        this.add(panel);

        this.setVisible(false);
        this.setVisible(true);
    }
}
