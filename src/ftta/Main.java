package ftta;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.shape.Rectangle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import javafx.scene.input.DragEvent;

public class Main extends Application implements Initializable {

	private static TransactionReport ptList;
	private TransactionScanner tScanner = new TransactionScanner(ptList.getTlist());

	private Stage stage;
	private int stageID = 1;

	public void start(TransactionReport ptListIn) throws Exception {
		ptList = ptListIn;
		Scene scene = null;
		
		if (stageID == 1) {
			Parent root = FXMLLoader.load(getClass().getResource("DragDrop.fxml"));
			scene = new Scene(root, 570, 492);
		} else if (stageID == 2) {
			Parent root = FXMLLoader.load(getClass().getResource("Setup.fxml"));
			scene = new Scene(root, 570, 492);
		} else if (stageID == 3) {
			Parent root = FXMLLoader.load(getClass().getResource("tSort.fxml"));
			scene = new Scene(root, 570, 492);
		} else if (stageID == 4) {
			Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
			scene = new Scene(root, 570, 492);
		}
		
		this.stage = new Stage();
		stage.setTitle("Financial Tracker");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String args[]) {
		TransactionReport trReport = new TransactionReport();
		ptList = trReport;
		launch(args);
	}

	// **********************
	// ****** Controls ******
	// **********************

	@FXML
	private Rectangle rectangle;
	@FXML
	private TextField categoryText;
	@FXML
	private TextField buyerText;
	@FXML
	private ListView<String> addedList;
	@FXML
	private Button submitCategory;
	@FXML
	private ChoiceBox tagMenu;
	@FXML
	private Label reportLabel;
	@FXML
	private TextField tagName;

	// defaults

	// stage one
	@FXML
	private void handleDragOver(DragEvent event) {
		if (event.getDragboard().hasFiles()) {
			event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
		}
		event.consume();
	}

	@FXML
	public void handleDrop(DragEvent event) throws Exception {
		Dragboard db = event.getDragboard();
		boolean success = false;
		if (db.hasFiles()) {
			if (db.getFiles().toString().contains(".xls")) {
				File file = db.getFiles().get(0);
				ptList.setTlist(tScanner.ScanExcelSheet(ptList.getTlist(), file));
				stageID = 2;
				start(ptList);
				success = true;
			} else {
				success = false;
			}
		}
		event.setDropCompleted(success);
		event.consume();
	}

	// Stage Two
	@FXML
	private void nextButton() throws Exception {
		stageID = 3;
		start(ptList);
	}

	@FXML
	public void addCategory() throws Exception {
		if (submitCategory.onMouseClickedProperty() != null) {
			Category category = new Category(categoryText.getText());
			ptList.getClist().addCategory(category);
			reportLabel.setText("Category " + category.getName() + " has been added.");
			tagMenu.getItems().add(category.getName());
		}
	}

	@FXML
	public void addBuyer() throws Exception {
		if (submitCategory.onMouseClickedProperty() != null) {
			Buyer buyer = new Buyer(buyerText.getText());
			ptList.getBlist().addBuyerToList(buyer);
			reportLabel.setText("Buyer " + buyer.getInitials() + " has been added.");
		}
	}

	@FXML
	public void addTag() throws Exception {
		if (submitCategory.onMouseClickedProperty() != null) {
			ptList.getClist().getCategory(tagMenu.getValue().toString()).addTag(tagName.getText());
			reportLabel.setText("Tag " + tagName.getText() + " has been added to " + tagMenu.getValue());
		}
	}
	
	@FXML
	public void back() throws Exception {
		stageID = 2;
		start(ptList);
	}

	// stage three

	@FXML
	private ChoiceBox buyerList;
	@FXML
	private ChoiceBox tagList;
	@FXML
	private Label transactionLbl;
	@FXML
	private Label trCounter;
	@FXML
	private Button beginButton;
	@FXML
	private Button submitPT;
	@FXML
	private Button exportButton;
		
	private int transactionIndex = 0;


	@FXML
	public void updateStageThree() throws Exception {
		loadBuyers();
		loadTags();
		transactionCounter(transactionIndex);
		listTransaction(transactionIndex);
	}

	@FXML
	public void loadBuyers() throws Exception {
		for (int i = 0; i < ptList.getBlist().size(); i++) {
			buyerList.getItems().add(ptList.getBlist().getBuyerInitials(i));
		}
	}

	@FXML
	public void loadTags() throws Exception {
		for (int i = 0; i < ptList.getClist().size(); i++) {
			for (int j = 0; j < ptList.getClist().getCategory(i).getTagListSize(); j++) {
				tagList.getItems().add(ptList.getClist().getCategory(i).getTag(j));
			}
		}
	}

	@FXML
	public void listTransaction(int index) {
		transactionLbl.setText(ptList.getTlist().getTransaction(index).toString());
	}
	
	@FXML
	public void transactionCounter(int index) {
		trCounter.setText(Integer.toString(index+1) + "/"+ ptList.getTlist().size());
	}
	
	@FXML 
	public void submitTransaction() {
		if (transactionIndex < ptList.getTlist().size()) {
			ProcessedTransaction newProcessed = new ProcessedTransaction(ptList.getTlist().getTransaction(transactionIndex), buyerList.getValue().toString(), tagList.getValue().toString());
			ptList.addProcessedTransaction(newProcessed);
			transactionIndex++;
			listTransaction(transactionIndex);
			transactionCounter(transactionIndex);
		}
		
	}
	
	@FXML
	public void exportPTransactions() {
		FileWriter fw = new FileWriter(ptList.getPTList());
		fw.writeToFile("n");
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		TransactionReport trReport = new TransactionReport();
		//ptList = trReport;
		start(ptList);
	}

}