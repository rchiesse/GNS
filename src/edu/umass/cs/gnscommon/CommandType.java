/* Copyright (1c) 2015 University of Massachusetts
 * 
 * Licensed under the Apache License, Version 2.0 (1the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Initial developer(s): Westy */
package edu.umass.cs.gnscommon;

import edu.umass.cs.gnsclient.client.GNSCommand;
import edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.Clear;
import edu.umass.cs.gnsserver.main.GNSConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * All the commands supported by the GNS server are listed here.
 *
 * Each one of these has a corresponding method in an array defined in
 * edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.CommandDefs
 */
// We could probably dispense with the CommandDefs array (see above) and just
// put the classes in the enum
// once we upgrade older clients to not use the old command strings.
public enum CommandType {
  //
  // Data Commands
  //
//
  // Data Commands
  //
//
  // Data Commands
  //
//
  // Data Commands
  //
//
  // Data Commands
  //
//
  // Data Commands
  //
//
  // Data Commands
  //
//
  // Data Commands
  //
  Append(110, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.Append.class,
          GNSCommand.ResultType.NULL, true, false),
  AppendList(111, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.AppendList.class),
  AppendListSelf(112, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.AppendListSelf.class),
  AppendListUnsigned(113, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.AppendListUnsigned.class),
  AppendListWithDuplication(114, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.AppendListWithDuplication.class),
  AppendListWithDuplicationSelf(115, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.AppendListWithDuplicationSelf.class),
  AppendListWithDuplicationUnsigned(116, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.AppendListWithDuplicationUnsigned.class),
  //
  AppendOrCreate(120, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.AppendOrCreate.class),
  AppendOrCreateList(121, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.AppendOrCreateList.class),
  AppendOrCreateListSelf(122, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.AppendOrCreateListSelf.class),
  AppendOrCreateListUnsigned(123, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.AppendOrCreateListUnsigned.class),
  AppendOrCreateSelf(124, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.AppendOrCreateSelf.class),
  AppendOrCreateUnsigned(125, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.AppendOrCreateUnsigned.class),
  //
  AppendSelf(130, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.AppendSelf.class),
  AppendUnsigned(131, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.AppendUnsigned.class),
  AppendWithDuplication(132, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.AppendWithDuplication.class),
  AppendWithDuplicationSelf(133, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.AppendWithDuplicationSelf.class),
  AppendWithDuplicationUnsigned(134, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.AppendWithDuplicationUnsigned.class),
  //

  Clear(140, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.Clear.class),
  ClearSelf(141, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ClearSelf.class),
  ClearUnsigned(142, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ClearUnsigned.class),
  //
  Create(150, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.Create.class),
  CreateEmpty(151, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.CreateEmpty.class),
  CreateEmptySelf(152, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.CreateEmptySelf.class),
  CreateList(153, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.CreateList.class),
  CreateListSelf(154, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.CreateListSelf.class),
  CreateSelf(155, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.CreateSelf.class),
  //
  Read(160, Type.READ,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.Read.class,
          GNSCommand.ResultType.MAP, true, false),
  ReadSelf(161, Type.READ,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReadSelf.class,
          GNSCommand.ResultType.MAP),
  ReadUnsigned(162, Type.READ,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReadUnsigned.class,
          GNSCommand.ResultType.MAP),
  ReadMultiField(163, Type.READ,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReadMultiField.class,
          GNSCommand.ResultType.MAP),
  ReadMultiFieldUnsigned(164, Type.READ,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReadMultiFieldUnsigned.class,
          GNSCommand.ResultType.MAP),
  //
  ReadArray(170, Type.READ,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReadArray.class,
          GNSCommand.ResultType.MAP),
  ReadArrayOne(171, Type.READ,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReadArrayOne.class),
  ReadArrayOneSelf(172, Type.READ,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReadArrayOneSelf.class),
  ReadArrayOneUnsigned(173, Type.READ,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReadArrayOneUnsigned.class),
  ReadArraySelf(174, Type.READ,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReadArraySelf.class),
  ReadArrayUnsigned(175, Type.READ,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReadArrayUnsigned.class),
  //
  Remove(180, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.Remove.class),
  RemoveList(181, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.RemoveList.class),
  RemoveListSelf(182, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.RemoveListSelf.class),
  RemoveListUnsigned(183, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.RemoveListUnsigned.class),
  RemoveSelf(184, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.RemoveSelf.class),
  RemoveUnsigned(185, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.RemoveUnsigned.class),
  //
  Replace(190, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.Replace.class),
  ReplaceList(191, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReplaceList.class),
  ReplaceListSelf(192, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReplaceListSelf.class),
  ReplaceListUnsigned(193, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReplaceListUnsigned.class),
  //
  ReplaceOrCreate(210, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReplaceOrCreate.class),
  ReplaceOrCreateList(211, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReplaceOrCreateList.class),
  ReplaceOrCreateListSelf(212, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReplaceOrCreateListSelf.class),
  ReplaceOrCreateListUnsigned(213, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReplaceOrCreateListUnsigned.class),
  ReplaceOrCreateSelf(214, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReplaceOrCreateSelf.class),
  ReplaceOrCreateUnsigned(215, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReplaceOrCreateUnsigned.class),
  ReplaceSelf(216, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReplaceSelf.class),
  ReplaceUnsigned(217, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReplaceUnsigned.class),
  //
  ReplaceUserJSON(220, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReplaceUserJSON.class),
  ReplaceUserJSONUnsigned(221, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.ReplaceUserJSONUnsigned.class),
  //
  CreateIndex(230, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.CreateIndex.class),
  //
  Substitute(231, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.Substitute.class),
  SubstituteList(232, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.SubstituteList.class),
  SubstituteListSelf(233, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.SubstituteListSelf.class),
  SubstituteListUnsigned(234, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.SubstituteListUnsigned.class),
  SubstituteSelf(235, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.SubstituteSelf.class),
  SubstituteUnsigned(236, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.SubstituteUnsigned.class),
  //
  RemoveField(240, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.RemoveField.class),
  RemoveFieldSelf(241, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.RemoveFieldSelf.class),
  RemoveFieldUnsigned(242, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.RemoveFieldUnsigned.class),
  //
  Set(250, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.Set.class),
  SetSelf(251, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.SetSelf.class),
  SetFieldNull(252, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.SetFieldNull.class),
  SetFieldNullSelf(253, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.SetFieldNullSelf.class),
  //
  // Select Commands
  //
  Select(310, Type.SELECT,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.select.Select.class),
  SelectGroupLookupQuery(311, Type.SELECT,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.select.SelectGroupLookupQuery.class),
  SelectGroupSetupQuery(312, Type.SELECT,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.select.SelectGroupSetupQuery.class),
  SelectGroupSetupQueryWithGuid(313, Type.SELECT,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.select.SelectGroupSetupQueryWithGuid.class),
  SelectGroupSetupQueryWithGuidAndInterval(314, Type.SELECT,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.select.SelectGroupSetupQueryWithGuidAndInterval.class),
  SelectGroupSetupQueryWithInterval(315, Type.SELECT,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.select.SelectGroupSetupQueryWithInterval.class),
  //
  SelectNear(320, Type.SELECT,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.select.SelectNear.class,
          GNSCommand.ResultType.LIST),
  SelectWithin(321, Type.SELECT,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.select.SelectWithin.class,
          GNSCommand.ResultType.LIST),
  SelectQuery(322, Type.SELECT,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.select.SelectQuery.class),
  //
  // Account Commands
  //
  AddAlias(410, Type.CREATE_DELETE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.account.AddAlias.class),
  AddGuid(411, Type.CREATE_DELETE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.account.AddGuid.class),
  AddMultipleGuids(412, Type.CREATE_DELETE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.account.AddMultipleGuids.class),
  AddMultipleGuidsFast(413, Type.CREATE_DELETE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.account.AddMultipleGuidsFast.class),
  AddMultipleGuidsFastRandom(414, Type.CREATE_DELETE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.account.AddMultipleGuidsFastRandom.class),
  //
  LookupAccountRecord(420, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.account.LookupAccountRecord.class),
  LookupRandomGuids(421, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.account.LookupRandomGuids.class), // for testing

  LookupGuid(422, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.account.LookupGuid.class),
  LookupPrimaryGuid(423, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.account.LookupPrimaryGuid.class),
  LookupGuidRecord(424, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.account.LookupGuidRecord.class),
  //
  RegisterAccount(430, Type.CREATE_DELETE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.account.RegisterAccount.class),
  //RegisterAccountSansPassword(431, Type.CREATE_DELETE),
  RegisterAccountUnsigned(432, Type.CREATE_DELETE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.account.RegisterAccountUnsigned.class),
  //
  RemoveAccount(440, Type.CREATE_DELETE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.account.RemoveAccount.class),
  RemoveAlias(441, Type.CREATE_DELETE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.account.RemoveAlias.class),
  RemoveGuid(442, Type.CREATE_DELETE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.account.RemoveGuid.class),
  RemoveGuidNoAccount(443, Type.CREATE_DELETE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.account.RemoveGuidNoAccount.class),
  RetrieveAliases(444, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.account.RetrieveAliases.class,
          GNSCommand.ResultType.LIST),
  //
  SetPassword(450, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.account.SetPassword.class),
  VerifyAccount(451, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.account.VerifyAccount.class),
  //
  ResetKey(460, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.account.ResetKey.class),
  // ACL
  AclAdd(510, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.acl.AclAdd.class),
  AclAddSelf(511, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.acl.AclAddSelf.class),
  AclRemove(512, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.acl.AclRemove.class),
  AclRemoveSelf(513, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.acl.AclRemoveSelf.class),
  AclRetrieve(514, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.acl.AclRetrieve.class),
  AclRetrieveSelf(515, Type.UPDATE,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.acl.AclRetrieveSelf.class),
  // Group
  AddMembersToGroup(610, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.group.AddMembersToGroup.class),
  AddMembersToGroupSelf(611, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.group.AddMembersToGroupSelf.class),
  AddToGroup(612, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.group.AddToGroup.class),
  AddToGroupSelf(613, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.group.AddToGroupSelf.class),
  GetGroupMembers(614, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.group.GetGroupMembers.class,
          GNSCommand.ResultType.LIST),
  GetGroupMembersSelf(615, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.group.GetGroupMembersSelf.class),
  GetGroups(616, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.group.GetGroups.class,
          GNSCommand.ResultType.LIST),
  GetGroupsSelf(617, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.group.GetGroupsSelf.class),
  //
  RemoveFromGroup(620, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.group.RemoveFromGroup.class),
  RemoveFromGroupSelf(621, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.group.RemoveFromGroupSelf.class),
  RemoveMembersFromGroup(622, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.group.RemoveMembersFromGroup.class),
  RemoveMembersFromGroupSelf(623, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.group.RemoveMembersFromGroupSelf.class),
  // Admin
  Help(710, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.Help.class),
  HelpTcp(711, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.HelpTcp.class),
  HelpTcpWiki(712, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.data.HelpTcpWiki.class),
  Admin(715, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.admin.Admin.class),
  Dump(716, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.admin.Dump.class),
  //
  GetParameter(720, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.admin.GetParameter.class),
  SetParameter(721, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.admin.SetParameter.class),
  ListParameters(722, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.admin.ListParameters.class),
  DeleteAllRecords(723, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.admin.DeleteAllRecords.class),
  ResetDatabase(724, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.admin.ResetDatabase.class),
  ClearCache(725, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.admin.ClearCache.class),
  DumpCache(726, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.admin.DumpCache.class),
  //
  ChangeLogLevel(730, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.admin.ChangeLogLevel.class),
  AddTag(731, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.admin.AddTag.class),
  RemoveTag(732, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.admin.RemoveTag.class),
  ClearTagged(733, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.admin.ClearTagged.class),
  GetTagged(734, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.admin.GetTagged.class),
  ConnectionCheck(737, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.admin.ConnectionCheck.class),
  // Active code
  SetCode(810, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.activecode.SetCode.class),
  ClearCode(811, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.activecode.ClearCode.class),
  GetCode(812, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.activecode.GetCode.class),
  // Catch all for parsing errors
  Unknown(999, Type.OTHER,
          edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.admin.Unknown.class);

  private final int number;
  private final Type category;
  private final Class<?> commandClass;
  private final GNSCommand.ResultType returnType;

  /* arun: Adding more fields below for documentation and invariant assertion
	 * purposes. */
  /**
   * True means that this command can be safely coordinated by RemoteQuery at
   * servers. True must mean that it is never the case that execute(name1) for
   * any canBeSafelyCoordinated command never remote-query-invokes this
   * command also for name1.
   */
  private boolean canBeSafelyCoordinated;

  /**
   * Commands that are not intended to be run by rogue clients.
   */
  private boolean notForRogueClients;

  /**
   * Other commands that may be remote-query-invoked by this command.
   * Non-final only because we can not name enums before they are defined.
   */
  private CommandType[] commandTypes;

  private void setChain(CommandType... commandTypes) {
    if (this.commandTypes == null) {
      this.commandTypes = commandTypes;
    } else {
      throw new RuntimeException("Can set command chain exactly once");
    }
  }

  /* Westy: please fill these in. The chain of any command must contain a list
	 * of all commands that the execution of that command MAY invoke. For
	 * example, if AddGuid may (not necessarily every time) invoke LookupGuid as
	 * a step, LookupGuid belongs to AddGuid's chain. The list interpretation is
	 * somewhat loose as the call chain is really a DAG, but it is good to
	 * flatten the DAG in breadth-first order, e.g., if A->B and B->C and B->D
	 * and D->E, where "->" means "invokes", the chain of A is {B,C,D,E}. It is
	 * okay to stop recursively unraveling a chain, e.g., stop at A->B, if what
	 * follows is identical to B's chain.
	 * 
	 * Hopefully there are no cycles in these chains. Assume standard execution
	 * for all commands, i.e., with no active code enabled, while listing chains.
   */
  static {
    Read.setChain(ReadUnsigned);
    AddGuid.setChain(LookupGuid, ReplaceUserJSONUnsigned); // what else?
  }

  public enum Type {
    READ, UPDATE, CREATE_DELETE, SELECT, OTHER
  }

//  private CommandType(int number, Type category) {
//    this(number, category, GNSCommand.ResultType.STRING);
//  }
  private CommandType(int number, Type category, Class<?> commandClass) {
    this(number, category, commandClass, GNSCommand.ResultType.STRING);
  }

//  private CommandType(int number, Type category,
//          GNSCommand.ResultType returnType) {
//    this(number, category, GNSCommand.ResultType.STRING,
//            category == Type.READ || category == Type.UPDATE, false);
//
//  }
  private CommandType(int number, Type category, Class<?> commandClass,
          GNSCommand.ResultType returnType) {
    this(number, category, commandClass, GNSCommand.ResultType.STRING,
            category == Type.READ || category == Type.UPDATE, false);

  }

//  private CommandType(int number, Type category,
//          GNSCommand.ResultType returnType, boolean canBeSafelyCoordinated,
//          boolean notForRogueClients) {
//    this.number = number;
//    this.category = category;
//    this.returnType = returnType;
//
//    this.canBeSafelyCoordinated = canBeSafelyCoordinated;
//
//    // presumably every command is currently available to thugs
//    this.notForRogueClients = notForRogueClients;
//
//  }
  private CommandType(int number, Type category, Class<?> commandClass,
          GNSCommand.ResultType returnType, boolean canBeSafelyCoordinated,
          boolean notForRogueClients) {
    this.number = number;
    this.category = category;
    this.commandClass = commandClass;
    this.returnType = returnType;

    this.canBeSafelyCoordinated = canBeSafelyCoordinated;

    // presumably every command is currently available to thugs
    this.notForRogueClients = notForRogueClients;

  }

  public int getInt() {
    return number;
  }

  // add isCoordinated
  // add strictly local flag or remote flag
  // what are the set of commands that will be invoked by this command
  // AKA multi-transactional commands
  public boolean isRead() {
    return category.equals(Type.READ);
  }

  public boolean isUpdate() {
    return category.equals(Type.UPDATE);
  }

  public boolean isCreateDelete() {
    return category.equals(Type.CREATE_DELETE);
  }

  public boolean isSelect() {
    return category.equals(Type.SELECT);
  }

  private static final Map<Integer, CommandType> map = new HashMap<Integer, CommandType>();

  static {
    for (CommandType type : CommandType.values()) {
      if (!type.getCommandClass().getSimpleName().equals(type.name())) {
        GNSConfig.getLogger().log(Level.WARNING,
                "Enum name should be the same as implmenting class: {0} vs. {1}",
                new Object[]{type.getCommandClass().getSimpleName(), type.name()});
      }
      if (map.containsKey(type.getInt())) {
        GNSConfig.getLogger().warning(
                "**** Duplicate number for command type " + type + ": "
                + type.getInt());
      }
      map.put(type.getInt(), type);
    }
  }

  public static CommandType getCommandType(int number) {
    return map.get(number);
  }

  public Class<?> getCommandClass() {
    return commandClass;
  }

  public GNSCommand.ResultType getResultType() {
    return this.returnType;
  }

  private static String insertUnderScoresBeforeCapitals(String str) {
    StringBuilder result = new StringBuilder();
    // SKIP THE FIRST CAPITAL
    result.append(str.charAt(0));
    // START AT ONE SO WE SKIP THE FIRST CAPITAL
    for (int i = 1; i < str.length(); i++) {
      if (Character.isUpperCase(str.charAt(i))) {
        result.append("_");
      }
      result.append(str.charAt(i));
    }
    return result.toString();
  }

  private static String generateSwiftConstants() {
    StringBuilder result = new StringBuilder();
    for (CommandType commandType : CommandType.values()) {
      result.append("    public static let ");
      result.append(insertUnderScoresBeforeCapitals(
              commandType.toString()).toUpperCase());
      result.append("\t\t\t\t = ");
      result.append("\"");
      result.append(commandType.toString());
      result.append("\"\n");
    }
    return result.toString();
  }

  public static void main(String args[]) {
    System.out.println(generateSwiftConstants());

  }
}
