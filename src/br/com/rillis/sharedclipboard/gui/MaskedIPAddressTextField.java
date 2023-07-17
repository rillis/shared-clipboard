package br.com.rillis.sharedclipboard.gui;

import javax.swing.*;
import javax.swing.text.*;
import java.util.Arrays;

public class MaskedIPAddressTextField extends JTextField {
    private static final int MAX_LENGTH = 15; // Maximum length of the masked IP address

    public MaskedIPAddressTextField() {

        setDocument(new MaskedIPAddressDocument());
        setText("192.168.0.");
    }

    private class MaskedIPAddressDocument extends PlainDocument {
        private boolean isUpdating = false; // Flag to prevent recursive updates

        @Override
        public void insertString(int offset, String text, AttributeSet attr) throws BadLocationException {
            if (isUpdating) {
                super.insertString(offset, text, attr);
                return;
            }

            // Remove any non-digit characters from the input
            StringBuilder filteredText = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (Character.isDigit(c) || c == '.' || c == ',') {
                    filteredText.append(c);
                }
            }

            // Insert the filtered text
            if (filteredText.length() > 0) {
                isUpdating = true;
                super.insertString(offset, filteredText.toString(), attr);
                formatIPAddress();
                isUpdating = false;
            }
        }

        private void formatIPAddress() {
            try {
                String text = getText(0, getLength());
                text = text.replace(",",".");
                StringBuilder formattedText = new StringBuilder();

                // Add periods after each segment
                for (int i = 0; i < Math.min(MAX_LENGTH, text.length()); i++) {
                    formattedText.append(text.charAt(i));
                }

                // Update the text field
                String result = formattedText.toString();

                replace(0, getLength(), result, null);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }
}

