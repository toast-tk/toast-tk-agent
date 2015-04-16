package com.synaptix.toast.plugin.synaptix.runtime.handler;

import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.SwingUtilities;

import org.fest.swing.core.MouseButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sncf.fret.swi.client.assemblage.helper.GestionPrevisionsHelper.Jour;
import com.sncf.fret.swi.client.assemblage.view.swing.extension.panel.gestionprevisions.CenterCellsPanel;
import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.fixture.utils.FestRobotInstance;
import com.synaptix.toast.plugin.synaptix.runtime.handler.action.CenterCellsPanelDoClickAction;
import com.synaptix.toast.plugin.synaptix.runtime.handler.action.CenterCellsPanelDoDoubleClickAction;
import com.synaptix.toast.plugin.synaptix.runtime.handler.action.CenterCellsPanelDoOpenMenuAction;
import com.synaptix.toast.plugin.synaptix.runtime.interpreter.EventTransformer;

public final class CenterCellsHandler extends AbstractSwingCustomWidgetHandler {

	private static final Logger LOG = LoggerFactory.getLogger(CenterCellsHandler.class);
	
	private final CenterCellsPanel centerCellsPanel;
	
	private final String command;
	
	private final ActionCenterCellsInfo actionCenterCellsInfo;
	
	CenterCellsHandler(
			final CenterCellsPanel centerCellsPanel,
			final CommandRequest commandRequest
	) {
		this.centerCellsPanel = centerCellsPanel;
		this.command = commandRequest.value;
		this.actionCenterCellsInfo = new ActionCenterCellsInfo(command);
	}
	
	@Override
	public String handleCommand() {
		try {
			return handleCommandCenterCellsPanel();
		} 
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	private String handleCommandCenterCellsPanel() {
		if(command.startsWith(EventTransformer.CLIQUER_SUR)) {
			handleCliquerSurCommand();
		}
		else if(command.startsWith(EventTransformer.DOUBLE_CLIQUER_SUR)) {
			handleDoubleCliquerSurCommand();
		}
		else if(command.startsWith(EventTransformer.OUVRIR_LE_MENU_SUR)) {
			handleOuvrirLeMenuSurCommand();
		}
		else if(command.startsWith(EventTransformer.GET)) {
			return handleGetCommand();
		}
		else if(command.startsWith(EventTransformer.SET)) {
			handleSetCommand();
		}
		else {
			throw new IllegalAccessError("unknown action for CenterCellsPanel");
		}
		return null;
	}

	private void handleSetCommand() {
		LOG.info("set ({}:{}) {}", actionCenterCellsInfo.centerCellsPanelName, actionCenterCellsInfo.nomFlux, actionCenterCellsInfo.datePrevision);
		final int col = findColumnFromDate(Calendar.getInstance(), actionCenterCellsInfo.datePrevision);
		LOG.info("col {} from {}", Integer.valueOf(col), actionCenterCellsInfo.datePrevision);
		centerCellsPanel.setActifValue(col, actionCenterCellsInfo.nomFlux, 1);
	}

	private String handleGetCommand() {
		final int col = findColumnFromDate(Calendar.getInstance(), actionCenterCellsInfo.datePrevision);
		LOG.info("get ({}:{}) {}", actionCenterCellsInfo.centerCellsPanelName, actionCenterCellsInfo.nomFlux, actionCenterCellsInfo.datePrevision);
		final int actifValue = centerCellsPanel.getActifValue(col, actionCenterCellsInfo.nomFlux);
		LOG.info("finded actifValue  {}", Integer.valueOf(actifValue));
		return String.valueOf(actifValue);
	}

	private void handleOuvrirLeMenuSurCommand() {
		final Point pointToClick = findPointToClick();
		LOG.info("pointToClick = {}", pointToClick);
		centerCellsPanelDoOpenMenu(pointToClick);
	}

	private void handleDoubleCliquerSurCommand() {
		final Point pointToClick = findPointToClick();
		LOG.info("pointToClick = {}", pointToClick);
		centerCellsPanelDoDoubleClick(pointToClick);
	}

	private void handleCliquerSurCommand() {
		final Point pointToClick = findPointToClick();
		LOG.info("pointToClick = {}", pointToClick);
		centerCellsPanelDoClick(pointToClick);
	}

	private Point findPointToClick() {
		LOG.info("cliquer sur ({}:{}) {}|{}", actionCenterCellsInfo.centerCellsPanelName, actionCenterCellsInfo.nomFlux, actionCenterCellsInfo.datePrevision);
		final int row = findColumnFromDate(Calendar.getInstance(), actionCenterCellsInfo.datePrevision);
		final Rectangle changeSelection = centerCellsPanel.changeSelection(actionCenterCellsInfo.nomFlux, row, false, false);
		return new Point(changeSelection.x + changeSelection.width / 2, changeSelection.y + changeSelection.height / 2);
	}

	@SuppressWarnings("null")
	private int findColumnFromDate(
			final Calendar cal,
			final Date datePrevision
	) {
		final DateFormat df = new SimpleDateFormat("dd/MM/yy");
		final List<Jour> jours = centerCellsPanel.getJours();
		final int nbJours = jours != null ? jours.size() : 0;
		for(int index = 0; index < nbJours; ++index) {
			final Jour currentJour = jours.get(index);
			final Date currentDate = currentJour.date;
			if(isSameDate(datePrevision, df, currentDate)) {
				LOG.info("finded date = {}", currentDate);
				return index;
			}
		}
		return -1;
	}

	private static boolean isSameDate(
			final Date datePrevision, 
			final DateFormat df,
			final Date currentDate
	) {
		return df.format(datePrevision).equals(df.format(currentDate));
	}

	private void centerCellsPanelDoDoubleClick(final Point pointToClick) {
		try {
			new CenterCellsPanelDoDoubleClickAction(centerCellsPanel, pointToClick).run();;
		} 
		catch (final Exception e) {
			LOG.error(e.getMessage(), e);
		} 
	}
	
	private void centerCellsPanelDoClick(final Point pointToClick) {
		try {
			new CenterCellsPanelDoClickAction(centerCellsPanel, pointToClick).run();
		} 
		catch (final Exception e) {
			LOG.error(e.getMessage(), e);
		} 
	}
	
	private void centerCellsPanelDoOpenMenu(final Point pointToClick) {
		try {
			LOG.info("centerCellsPanelDoOpenMenu pointToClick = {}", pointToClick);
			new CenterCellsPanelDoOpenMenuAction(centerCellsPanel, pointToClick).run();
		} 
		catch (final Exception e) {
			LOG.error(e.getMessage(), e);
		} 
	}
}