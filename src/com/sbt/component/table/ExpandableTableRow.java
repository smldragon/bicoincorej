package com.sbt.component.table;

import com.sun.javafx.scene.control.skin.TableRowSkin;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.function.Function;

public class ExpandableTableRow<S extends SbtTableRowData> extends TableRow<S>  {

    private static final Color[] DefaultRowAlternativeColors = {Color.rgb(249,249,249,1.0),Color.rgb(240,240,240,1.0)};
    private ChangeListener<S> itemListener;
    private SoftReference<Pane> expandPaneRef;
    private final IntegerProperty leadingSpace = new SimpleIntegerProperty(8);
    private static final int bottomPadding = 8;
    private static final Logger logger = LoggerFactory.getLogger(ExpandableTableRow.class);
    private static final String expanPanelBckColorValue = "#ffffff";

    public ExpandableTableRow() {
        this(false);
        leadingSpace.addListener((observable, oldValue,newValue) -> {
            positionPanel(getSbtRowExpandPanel(),newValue.intValue());
        });
    }
    private ExpandableTableRow(boolean isExpanded) {
        itemListener = getItemListener();
        itemProperty().addListener( new WeakChangeListener(itemListener));
    }
    public Function<ExpandableTableRow<S>,Region> getDetailRegionCreater() {

        TableView<S> tableView = getTableView();

        if ( tableView instanceof SbtTableView) {
            return ((SbtTableView)tableView).getDetailRegionCreater();
        } else {
            return null;
        }
    }
    @Override
    protected ExpandableTableRowSkin<S> createDefaultSkin() {
        return new ExpandableTableRowSkin<S> (this);
    }
    public Pane getSbtRowExpandPanel() {
        if ( null == expandPaneRef || null == expandPaneRef.get()) {
            Function<ExpandableTableRow<S>,Region> detailRegionCreater = getDetailRegionCreater();
            Region expandPanel = detailRegionCreater.apply(this);
            Double panelWidth = expandPanel.prefWidthProperty().get();
            Double panelHeight = expandPanel.prefHeightProperty().get();
            if ( null == panelWidth || 0>= panelWidth || null == panelHeight || 0>= panelHeight) {
                Exception e = new Exception(" Width or height is not defined");
                logger.error("",e);
            }
            HBox panelWrapper = new HBox();
            panelWrapper.prefWidthProperty().set(panelWidth);
            panelWrapper.prefHeightProperty().set(panelHeight);
            panelWrapper.setStyle(" -fx-border-width: 0px 0px 0px 0px; -fx-opacity: 0.6; ");
            expandPanel.setStyle("  -fx-border-width: 0px; -fx-border-color: rgba(0,0,0,0,0.15); -fx-border-radius: 6px; ");
            panelWrapper.getChildren().add(expandPanel);
            expandPaneRef = new SoftReference<>(panelWrapper);
        }
        return expandPaneRef.get();
    }
    public void setLeadingSpace(int leadingSpace) {
        this.leadingSpace.set(leadingSpace);
    }
    public int getLeadingSpace() {
        return leadingSpace.get();
    }
    public void positionPanel(Pane pane,int leadingSpaceValue) {
        pane.setPadding(new Insets(0,0,0,leadingSpaceValue));
    }
    public void restore() {
        List<Node> tableRowChildren = getChildren();
        tableRowChildren.remove(tableRowChildren.size()-1);
        SbtTableView<?> table = (SbtTableView<?>)getTableView();

        ExpandableTableRowSkin<?> colorSkin = (ExpandableTableRowSkin<?>)getSkin();
        colorSkin.setAlignment(Pos.CENTER);
        colorSkin.setFontStyle(" -fx-font-weight: normal; ");

        this.setStyle(" -fx-cell-size: "+(int)table.getDefaultRowHeight()+"px;");

    }
    public void showExpandPanel(Pane panelWrapper) {

        List<Node> tableRowChildren = getChildren();
        tableRowChildren.add(panelWrapper);

        double panelHeight = panelWrapper.prefHeightProperty().get();
        panelWrapper.resize(Integer.MAX_VALUE,(int)panelHeight);

        ExpandableTableRowSkin<?> colorSkin = (ExpandableTableRowSkin<?>)getSkin();
        colorSkin.setAlignment(Pos.TOP_CENTER);

        double defaultRowHeight = ((SbtTableView<?>)getTableView()).getDefaultRowHeight();
        this.setStyle(" -fx-cell-size: "+(panelHeight+(int)defaultRowHeight+bottomPadding)+"px;");

        double translateX;
        Node firstColumn = getChildren().get(0);
        if ( firstColumn instanceof RowNumberCell) {
            translateX = ((Region)firstColumn).getWidth();
        } else {
            translateX = 0;
        }

        double translateY = snappedTopInset()+ defaultRowHeight;
        panelWrapper.relocate(translateX,translateY);

        positionPanel(panelWrapper,getLeadingSpace());
    }
    @Override
    public void updateIndex(int index) {
        int oldIndex = this.getIndex();
        super.updateIndex(index);
        if ( 0 > oldIndex || 0 > index) {
            //when hiding a column , this can happen
            return;
        }
        SbtTableView<S> table = (SbtTableView<S>)getTableView();
        expandPaneRef = null;
        if ( table.isExpandable() && getChildren().size() > 0) {
            if ( isExpanded()) {
                showExpandPanel(getSbtRowExpandPanel());
            }else if ( null != getSkin() ) {
                restore();
            }

        }
    }
    private static Color getRowColor(int rowIndex) {
        int colorIndex = rowIndex % 2 ;
        return DefaultRowAlternativeColors[colorIndex];
    }
    private static String convertToString(Color color ) {
        return "rgba("+(int)(color.getRed()*254)+","+(int)(color.getGreen()*254)+","+(int)(color.getBlue()*254)+", 0.8)";
    }
    protected ChangeListener<S> getItemListener() {
        return (observable,oldValue,newValue)-> {
            onItemUpdate(oldValue,newValue);
        };
    }
    protected void onItemUpdate(S oldValue,S newValue) {
        //updateSize(null,null);
    }
    public boolean isExpanded() {
        if ( null == getItem()) {
            return false;
        }
        return getItem().isExpanded();
    }
}
